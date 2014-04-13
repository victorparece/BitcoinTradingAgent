import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by vic on 4/7/14.
 */
public class StrategyEvaluator
{
    private ArrayList<Strategy> strategies = new ArrayList<Strategy>();

    private static String DATA_FILE = "data/btceUSD.csv";
    private static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

    public StrategyEvaluator(DateTime currentDateTime, DateTime endDateTime)
    {
        //Initialize strategies
        //TODO: market simulated should probably be copied to improve performance
        //strategies.add(new Strategy_MarketOrder(new MarketSimulator(DATA_FILE, currentDateTime, endDateTime)));
        strategies.add(new Strategy_SubmitAndLeave(new MarketSimulator(DATA_FILE, currentDateTime, endDateTime)));
    }

    public ArrayList<Strategy> StartEvaluation()
    {
        //Evaluate all strategies
        for (Strategy strategy : strategies)
            strategy.Evaluate();

        return strategies;
    }

    public static void main(String[ ] args)
    {
        DateTime currentDateTime = new DateTime(DATETIME_FORMATTER.parseDateTime("01/01/2012 12:00:00"), DateTimeZone.UTC);
        DateTime endDateTime = new DateTime(DATETIME_FORMATTER.parseDateTime("01/05/2012 12:00:00"), DateTimeZone.UTC);

        StrategyEvaluator strategyEvaluator = new StrategyEvaluator(currentDateTime, endDateTime);
        ArrayList<Strategy> strategies = strategyEvaluator.StartEvaluation();

        //TODO: Loop through evaluations and print results
    }

}
