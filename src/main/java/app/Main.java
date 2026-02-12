package app;

import model.dto.ParkingSpotRecord;
import model.enums.SpotStatus;
import model.enums.SpotType;
import repository.Database;
import repository.ParkingSpotRepository;
import ui.MainFrame;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Database database = new Database();
        database.init();

        ParkingSpotRepository parkingSpotRepository = new ParkingSpotRepository(database);

        int seededCount = 0;
        if (parkingSpotRepository.isEmpty()) {
            List<ParkingSpotRecord> seedSpots = new ArrayList<>();
            for (int floor = 1; floor <= 5; floor++) {
                for (int row = 1; row <= 3; row++) {
                    for (int spot = 1; spot <= 10; spot++) {
                        SpotType type;
                        double rate;
                        if (spot <= 3) {
                            type = SpotType.COMPACT;
                            rate = 1.0;  // RM1/hour - Motorcycle/compact spaces
                        } else if (spot <= 8) {
                            type = SpotType.REGULAR;
                            rate = 3.0;  // RM3/hour - Standard Malaysian mall rate
                        } else if (spot == 9) {
                            type = SpotType.HANDICAPPED;
                            rate = 0.0;  // FREE for OKU (Malaysian standard)
                        } else {
                            type = SpotType.RESERVED;
                            rate = 4.0;  // RM4/hour - Season/reserved parking
                        }

                        String spotId = String.format("F%d-R%d-S%d", floor, row, spot);
                        seedSpots.add(new ParkingSpotRecord(
                                spotId,
                                floor,
                                row,
                                spot,
                                type,
                                rate,
                                SpotStatus.AVAILABLE,
                                null
                        ));
                    }
                }
            }
            parkingSpotRepository.insertMany(seedSpots);
            seededCount = seedSpots.size();
        }

        System.out.println("DB ready");
        System.out.println("Total spots seeded: " + seededCount);

        boolean displayUnavailable = !System.getProperty("os.name", "").toLowerCase().contains("win")
                && System.getenv("DISPLAY") == null
                && System.getenv("WAYLAND_DISPLAY") == null;

        if (GraphicsEnvironment.isHeadless() || displayUnavailable) {
            System.out.println("Headless environment detected. UI launch skipped.");
            return;
        }

        SwingUtilities.invokeLater(() -> new MainFrame(database).setVisible(true));
    }
}
