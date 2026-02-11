package app;

import model.dto.ParkingSpotRecord;
import model.enums.SpotStatus;
import model.enums.SpotType;
import repository.Database;
import repository.ParkingSpotRepository;

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
                            rate = 2.0;
                        } else if (spot <= 8) {
                            type = SpotType.REGULAR;
                            rate = 5.0;
                        } else if (spot == 9) {
                            type = SpotType.HANDICAPPED;
                            rate = 2.0;
                        } else {
                            type = SpotType.RESERVED;
                            rate = 10.0;
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
    }
}