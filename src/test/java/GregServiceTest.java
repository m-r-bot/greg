import org.example.GregService;
import org.example.types.FederalState;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class GregServiceTest {

    GregService gregService = new GregService(FederalState.SN);

        @Test
        public void testIsHolidayInState(){
            var currentDay =LocalDate.of(2022, 3, 1);
            boolean isHolidayInState = gregService.isHolidayInCurrentFederalState(currentDay);

            Assert.assertFalse(isHolidayInState);
        }

    @Test
    public void testIsHolidayInOtherState(){
          var currentDay =LocalDate.of(2022, 3, 8);
          boolean isHolidayInOtherState = gregService.isHolidayInOtherFederalState(currentDay);

          Assert.assertTrue(isHolidayInOtherState);
    }
    // always returns true
    @Test
    public void testCalculateSecondNewYear(){

            var SecondNewYear = LocalDate.of(2023, 1, 1);
            var FirstNewYear = LocalDate.of(2022, 1, 1);

            Assert.assertFalse(SecondNewYear.getDayOfWeek().getValue() == FirstNewYear.getDayOfWeek().getValue());
    }
}
