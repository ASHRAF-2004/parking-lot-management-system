package service;

import model.core.ExitBill;
import model.core.Payment;
import model.dto.ActiveParkingRecord;
import model.dto.ParkingSpotRecord;
import model.enums.FineReason;
import model.enums.FineSchemeType;
import model.enums.PaymentMethod;
import model.enums.SpotStatus;
import repository.ActiveParkingRepository;
import repository.Database;
import repository.ParkingHistoryRepository;
import repository.ParkingSpotRepository;
import repository.FineRepository;
import service.rules.DurationCalculator;
import service.rules.RateCalculator;
import util.MoneyUtil;
import util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExitService {
    private final ActiveParkingRepository activeParkingRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final FineService fineService;
	private final FineRepository fineRepository;
    private final ParkingHistoryRepository parkingHistoryRepository;
    private final Map<String, ExitBill> billCache = new HashMap<>();

    public ExitService(Database database) {
        this.activeParkingRepository = new ActiveParkingRepository(database);
        this.parkingSpotRepository = new ParkingSpotRepository(database);
        this.fineService = new FineService(database);
		this.fineRepository = new FineRepository(database);
        this.parkingHistoryRepository = new ParkingHistoryRepository(database);
    }

    public ExitBill buildBill(String plate) {
        String normalizedPlate = normalizePlate(plate);
        if (billCache.containsKey(normalizedPlate)) {
            return billCache.get(normalizedPlate);
        }

        ActiveParkingRecord active = getActive(normalizedPlate);
        ParkingSpotRecord spot = getSpot(active.getSpotId());

        LocalDateTime exitTime = TimeUtil.now();
        int hours = DurationCalculator.computeBillableHours(active.getEntryTime(), exitTime);
        double hourlyRate = RateCalculator.computeHourlyRate(
                active.getVehicleType(),
                active.isHasHandicappedCard(),
                spot.getType(),
                spot.getRate()
        );
        double parkingFee = MoneyUtil.round2(hours * hourlyRate);
        
        // Apply Malaysian daily cap (max RM20 per day)
        parkingFee = MoneyUtil.round2(RateCalculator.applyDailyCap(parkingFee, hours));

        double unpaidBefore = MoneyUtil.round2(fineService.getUnpaidAmount(active.getPlate()));
        double newFine = 0.0;
		
		String createdDatePrefix = LocalDate.now().toString();
        // Malaysian standard: Overstay fine beyond 12 hours (not 24)
        if (hours > 12) {
            FineSchemeType scheme = fineService.getCurrentScheme();
            double overstayFine = MoneyUtil.round2(fineService.computeOverstayFine(hours));
            if (overstayFine > 0 && !fineRepository.existsUnpaidFine(active.getPlate(), FineReason.OVERSTAY, createdDatePrefix)) {
                fineService.createFine(active.getPlate(), FineReason.OVERSTAY, overstayFine, scheme);
                newFine += overstayFine;
            }
        }

        if (spot.getType() == model.enums.SpotType.RESERVED
                && !active.isHasVipReservation()
                && !fineRepository.existsUnpaidFine(active.getPlate(), FineReason.RESERVED_MISUSE, createdDatePrefix)) {
            // Malaysian clamping equivalent fine: RM80
            fineService.createFine(active.getPlate(), FineReason.RESERVED_MISUSE, 80.0, fineService.getCurrentScheme());
            newFine += 80.0;
        }

        newFine = MoneyUtil.round2(newFine);
        double totalDue = MoneyUtil.round2(parkingFee + unpaidBefore + newFine);

        ExitBill bill = new ExitBill(
                active.getPlate(),
                active.getSpotId(),
                active.getEntryTime(),
                exitTime,
                hours,
                hourlyRate,
                parkingFee,
                unpaidBefore,
                newFine,
                totalDue
        );
        billCache.put(normalizedPlate, bill);
        return bill;
    }

    public Payment processPayment(String plate, PaymentMethod method, Double cashGiven, String cardNumber) {
        String normalizedPlate = normalizePlate(plate);
        ExitBill bill = billCache.containsKey(normalizedPlate) ? billCache.get(normalizedPlate) : buildBill(normalizedPlate);

        double totalDue = bill.getTotalDue();
        double normalizedCash = 0.0;
        String maskedCard = null;
        double change = 0.0;

        if (method == PaymentMethod.CASH) {
            if (cashGiven == null) {
                throw new IllegalArgumentException("Cash amount is required.");
            }
            normalizedCash = MoneyUtil.round2(cashGiven);
            if (normalizedCash < totalDue) {
                throw new IllegalArgumentException("Cash given must be >= total due.");
            }
            change = MoneyUtil.round2(normalizedCash - totalDue);
        } else if (method == PaymentMethod.CARD) {
            if (cardNumber == null || !cardNumber.matches("\\d{12,19}")) {
                throw new IllegalArgumentException("Card number must be 12-19 digits.");
            }
            String last4 = cardNumber.substring(cardNumber.length() - 4);
            maskedCard = "**** **** **** " + last4;
        } else if (method == PaymentMethod.TOUCH_N_GO || 
                   method == PaymentMethod.BOOST || 
                   method == PaymentMethod.GRABPAY || 
                   method == PaymentMethod.SHOPEEPAY || 
                   method == PaymentMethod.ONLINE_BANKING) {
            // eWallet/Online Banking - Store payment method name
            maskedCard = method.name().replace("_", " ");
        } else {
            throw new IllegalArgumentException("Unsupported payment method.");
        }

        ActiveParkingRecord active = getActive(normalizedPlate);

        fineService.markAllPaid(bill.getPlate());
        parkingSpotRepository.updateStatusAndPlate(bill.getSpotId(), SpotStatus.AVAILABLE, null);
        parkingHistoryRepository.insertHistoryRow(
                active.getPlate(),
                active.getVehicleType().name(),
                active.isHasHandicappedCard(),
                active.isHasVipReservation(),
                active.getSpotId(),
                active.getEntryTime(),
                bill.getExitTime(),
                bill.getHours(),
                bill.getHourlyRate(),
                bill.getParkingFee(),
                MoneyUtil.round2(bill.getUnpaidFinesBefore() + bill.getNewFineThisExit()),
                bill.getTotalDue(),
                method.name(),
                active.getTicketId()
        );
        activeParkingRepository.deleteByPlate(bill.getPlate());
        billCache.remove(normalizedPlate);

        return new Payment(method, totalDue, normalizedCash, maskedCard, change);
    }

    private String normalizePlate(String plate) {
        String normalized = plate == null ? "" : plate.trim().toUpperCase();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Plate is required.");
        }
        return normalized;
    }

    private ActiveParkingRecord getActive(String normalizedPlate) {
        ActiveParkingRecord active = activeParkingRepository.findByPlate(normalizedPlate);
        if (active == null) {
            throw new IllegalArgumentException("No active parking for plate: " + normalizedPlate);
        }
        return active;
    }

    private ParkingSpotRecord getSpot(String spotId) {
        ParkingSpotRecord spot = parkingSpotRepository.findById(spotId);
        if (spot == null) {
            throw new IllegalStateException("Spot not found for active parking: " + spotId);
        }
        return spot;
    }
}