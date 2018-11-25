package hackx.volvo.nebula.detect;

public class TopDetectorRunnable implements Runnable {
    private final int gougePx;
    private SpikeMeasurer _s;

    TopDetectorRunnable(SpikeMeasurer s, int gougePx) {
        this.gougePx = gougePx;
        this._s = s;
    }

    public void run() {
        double onePxAsMm = _s._plateSize/((_s._bottomPx - gougePx)*1.0);
        double oneMmAsPx = (_s._bottomPx - gougePx)/_s._plateSize;

        int spikeTopCrop = (int) (gougePx - ((_s._maxSpikeSizeMm * 1.25) * oneMmAsPx));
        int spikeBottomCrop = (int) (gougePx - ((_s._minSpikeSizeMm * 0.5) * oneMmAsPx));



        SpikeTopDetector s = new SpikeTopDetector(_s._rawImage, spikeTopCrop, spikeBottomCrop);

        int topPx = s.getTopPx();

        _s.addTopPx(topPx);
        _s.addValue((gougePx-topPx) * onePxAsMm);
    }
}
