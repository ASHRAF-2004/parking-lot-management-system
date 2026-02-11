package smoke;

import model.core.ExitBill;
import model.dto.ParkingSpotRecord;
import model.enums.PaymentMethod;
import model.enums.VehicleType;
import repository.ActiveParkingRepository;
import repository.Database;
import repository.ParkingSpotRepository;
import service.EntryService;
import service.ExitService;
import service.ParkingLotService;
import ui.MainFrame;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SmokeChecks {
    public static void main(String[] args) throws Exception {
        resetDatabase();

        Database database = new Database();
        database.init();

        verifyDatabaseInit(database);
        seedSpotsIfMissing(database);
        verifyMainFrameConstruction(database);
        verifyEntryExitFlow(database);

        System.out.println("Smoke checks passed.");
    }

    private static void resetDatabase() throws Exception {
        Files.deleteIfExists(Path.of("parking.db"));
    }

    private static void verifyDatabaseInit(Database database) {
        ParkingSpotRepository spotRepository = new ParkingSpotRepository(database);
        spotRepository.findAll();
    }

    private static void seedSpotsIfMissing(Database database) {
        app.Main.main(new String[0]);

        ParkingSpotRepository spotRepository = new ParkingSpotRepository(database);
        if (spotRepository.findAll().isEmpty()) {
            throw new IllegalStateException("Expected seeded spots after app.Main startup.");
        }
    }

    private static void verifyMainFrameConstruction(Database database) throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("MainFrame construction check skipped in headless environment.");
            return;
        }

        SwingUtilities.invokeAndWait(() -> {
            MainFrame frame = new MainFrame(database);
            frame.dispose();
        });
    }

    private static void verifyEntryExitFlow(Database database) {
        ParkingLotService parkingLotService = new ParkingLotService(database);
        EntryService entryService = new EntryService(database);
        ExitService exitService = new ExitService(database);
        ActiveParkingRepository activeParkingRepository = new ActiveParkingRepository(database);

        List<ParkingSpotRecord> availableSpots = parkingLotService.findAvailableSpotsFor(VehicleType.CAR);
        if (availableSpots.isEmpty()) {
            throw new IllegalStateException("No available spots for smoke test.");
        }

        String plate = "SMOKE-001";
        String spotId = availableSpots.get(0).getSpotId();
        entryService.registerEntry(plate, VehicleType.CAR, false, false, spotId);

        ExitBill bill = exitService.buildBill(plate);
        exitService.processPayment(plate, PaymentMethod.CASH, bill.getTotalDue(), null);

        if (activeParkingRepository.findByPlate(plate) != null) {
            throw new IllegalStateException("Vehicle should no longer be active after exit.");
        }
    }
}
