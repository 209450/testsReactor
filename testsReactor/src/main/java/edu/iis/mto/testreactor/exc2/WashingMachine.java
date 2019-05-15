package edu.iis.mto.testreactor.exc2;

import java.util.Objects;

public class WashingMachine {

    public static final Percentage AVERAGE_DEGREE = new Percentage(40.0d);
    public static final double MAX_WEIGTH_KG = 8;
    private final DirtDetector dirtDetector;
    private final Engine engine;
    private final WaterPump waterPump;

    public WashingMachine(DirtDetector dirtDetector, Engine engine, WaterPump waterPump) {
        this.dirtDetector = Objects.requireNonNull(dirtDetector, "dirtDetector == null");
        this.engine = Objects.requireNonNull(engine, "engine == null");
        this.waterPump = Objects.requireNonNull(waterPump, "waterPump == null");
    }

    public LaundryStatus start(LaundryBatch laundryBatch, ProgramConfiguration programConfiguration) {
        if (overweight(laundryBatch)) {
            return tooHeavy();
        }
        Program programToRun = specifyProgram(laundryBatch, programConfiguration);
        runProgram(laundryBatch, programToRun);
        spin(programConfiguration, laundryBatch.getType());
        return programFinished(programToRun);
    }

    private boolean overweight(LaundryBatch laundryBatch) {
        return laundryBatch.getWeightKg() > MAX_WEIGTH_KG;
    }

    private LaundryStatus tooHeavy() {
        return LaundryStatus.builder()
                            .withResult(Result.FAILURE)
                            .withErrorCode(ErrorCode.TOO_HEAVY)
                            .build();
    }

    private Program specifyProgram(LaundryBatch laundryBatch, ProgramConfiguration programConfiguration) {
        Program program = programConfiguration.getProgram();
        if (program == Program.AUTODETECT) {
            Percentage dirtDegreePercentage = dirtDetector.detectDirtDegree(laundryBatch);
            return calculateProgramBasedOnDirtDegree(dirtDegreePercentage);
        }
        return program;
    }

    private Program calculateProgramBasedOnDirtDegree(Percentage dirtDegreePercentage) {
        if (dirtDegreePercentage.isGreaterThan(AVERAGE_DEGREE)) {
            return Program.LONG;
        }
        return Program.MEDIUM;
    }

    private void runProgram(LaundryBatch laundryBatch, Program programToRun) {
        waterPump.pour(laundryBatch.getWeightKg());
        engine.runWashing(programToRun.getTimeInMinutes());
        waterPump.release();
    }

    private void spin(ProgramConfiguration programConfiguration, Material material) {
        if (programConfiguration.isSpin() && material != Material.DELICATE) {
            engine.spin();
        }
    }

    private LaundryStatus programFinished(Program programToRun) {
        return LaundryStatus.builder()
                            .withResult(Result.SUCCESS)
                            .withRunnedProgram(programToRun)
                            .build();
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WashingMachine that = (WashingMachine) o;
        return Objects.equals(dirtDetector, that.dirtDetector) && Objects.equals(engine, that.engine) && Objects.equals(waterPump,
                that.waterPump);
    }

    @Override public int hashCode() {
        return Objects.hash(dirtDetector, engine, waterPump);
    }
}
