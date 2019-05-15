package edu.iis.mto.testreactor.exc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class WashingMachineTest {

    @Mock private DirtDetector dirtDetector;
    @Mock private Engine engine;
    @Mock private WaterPump waterPump;
    private WashingMachine washingMachine;
    private LaundryBatch laundryBatch;
    private LaundryBatch.Builder builderLaundryBatch;

    private ProgramConfiguration programConfiguration;
    private ProgramConfiguration.Builder builderProgramConfiguration;

    private LaundryStatus laundryStatus;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        washingMachine = new WashingMachine(dirtDetector, engine, waterPump);

        builderLaundryBatch = LaundryBatch.builder();
        builderLaundryBatch = builderLaundryBatch.withWeightKg(1);
        builderLaundryBatch = builderLaundryBatch.withType(Material.COTTON);
        laundryBatch = builderLaundryBatch.build();

        builderProgramConfiguration = ProgramConfiguration.builder();
        builderProgramConfiguration = builderProgramConfiguration.withProgram(Program.AUTODETECT);
        builderProgramConfiguration = builderProgramConfiguration.withSpin(true);
        programConfiguration = builderProgramConfiguration.build();

    }

    @Test public void givenTooHeavyLaundryReturnLaundryStatusErrorObject() {
        laundryBatch = builderLaundryBatch.withWeightKg(1000).build();

        laundryStatus = LaundryStatus.builder()
                                    .withResult(Result.FAILURE)
                                    .withErrorCode(ErrorCode.TOO_HEAVY)
                                    .build();

        assertThat(washingMachine.start(laundryBatch,programConfiguration), is(laundryStatus));
    }

    @Test public void itCompiles() {
        assertThat(true, Matchers.equalTo(true));
    }


}
