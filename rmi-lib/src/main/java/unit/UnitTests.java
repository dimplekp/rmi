package unit;

import test.*;

/** Runs all unit tests.

    <p>
    Tests run are:
    <ul>
    <li>{@link rmi.unit.SampleUnitTest}</li>
    </ul>
 */
public class UnitTests
{
    /** Runs the tests.

        @param arguments Ignored.
     */
    public static void main(String[] arguments)
    {
        // Create the test list, the series object, and run the test series.
        @SuppressWarnings("unchecked")
        Class<? extends Test>[]     tests =
            new Class[] {rmi.SampleUnitTest.class};
        Series                      series = new Series(tests);
        SeriesReport                report = series.run(3, System.out);

        // Print the report and exit with an appropriate exit status.
        report.print(System.out);
        System.exit(report.successful() ? 0 : 2);
    }
}
