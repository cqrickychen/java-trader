package trader.service.ta.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.num.Num;


/**
 * RSV Indicator
 * </p>
 * 计算公式: (CLOSE-LLV(LOW,N33))/(HHV(HIGH,N33)-LLV(LOW,N33))*100
 */
public class RSVIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> indicator;

    private final int barCount;

    private Indicator<Num> maxPriceIndicator;

    private Indicator<Num> minPriceIndicator;

    private Num N100;

    public RSVIndicator(BarSeries timeSeries, int barCount) {
        this(new ClosePriceIndicator(timeSeries), barCount, new HighPriceIndicator(timeSeries), new LowPriceIndicator(
                timeSeries));
    }

    public RSVIndicator(Indicator<Num> indicator, int barCount,
            HighPriceIndicator maxPriceIndicator, LowPriceIndicator minPriceIndicator) {
        super(indicator);
        this.indicator = indicator;
        this.barCount = barCount;
        this.maxPriceIndicator = maxPriceIndicator;
        this.minPriceIndicator = minPriceIndicator;
        N100 = numOf(100);
    }

    @Override
    protected Num calculate(int index) {
        HighestValueIndicator highestHigh = new HighestValueIndicator(maxPriceIndicator, barCount);
        LowestValueIndicator lowestMin = new LowestValueIndicator(minPriceIndicator, barCount);

        Num highestHighPrice = highestHigh.getValue(index);
        Num lowestLowPrice = lowestMin.getValue(index);

        return indicator.getValue(index).minus(lowestLowPrice)
                .dividedBy(highestHighPrice.minus(lowestLowPrice))
                .multipliedBy(N100);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " barCount: " + barCount;
    }
}