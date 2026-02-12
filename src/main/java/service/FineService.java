package service;

import model.dto.FineRecord;
import model.enums.FineReason;
import model.enums.FineSchemeType;
import model.enums.FineStatus;
import repository.Database;
import repository.FineRepository;
import repository.SettingsRepository;
import service.fines.FixedFineScheme;
import service.fines.FineScheme;
import service.fines.HourlyFineScheme;
import service.fines.ProgressiveFineScheme;
import util.TimeUtil;

import java.util.List;

public class FineService {
    private final SettingsRepository settingsRepository;
    private final FineRepository fineRepository;

    public FineService(Database database) {
        this.settingsRepository = new SettingsRepository(database);
        this.fineRepository = new FineRepository(database);
    }

    public FineSchemeType getCurrentScheme() {
        return settingsRepository.getFineScheme();
    }

    public void setCurrentScheme(FineSchemeType type) {
        settingsRepository.setFineScheme(type);
    }

    public double computeOverstayFine(int hours) {
        FineScheme scheme = switch (getCurrentScheme()) {
            case FIXED -> new FixedFineScheme();
            case HOURLY -> new HourlyFineScheme();
            case PROGRESSIVE -> new ProgressiveFineScheme();
        };
        return scheme.compute(hours);
    }

    public void createFine(String plate, FineReason reason, double amount, FineSchemeType schemeAtTime) {
        fineRepository.insertFine(new FineRecord(
                0,
                plate,
                reason,
                schemeAtTime,
                amount,
                FineStatus.UNPAID,
                TimeUtil.now(),
                null
        ));
    }

    public double getUnpaidAmount(String plate) {
        return fineRepository.sumUnpaidAmountByPlate(plate);
    }

    public List<FineRecord> getOutstandingFines() {
        return fineRepository.findAllUnpaid();
    }

    public void markAllPaid(String plate) {
        fineRepository.markAllPaidForPlate(plate, TimeUtil.toIso(TimeUtil.now()));
    }
}