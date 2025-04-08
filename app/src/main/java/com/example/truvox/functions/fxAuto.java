package com.example.truvox.functions;

public class fxAuto {
    private final int tmin, tmax;
    private final float[] bias, ac;
    private final int srate;

    public fxAuto(float fxmin, float fxlow, float fxhigh, float fxmax, int sampleRate) {
        this.srate = sampleRate;
        this.tmin = Math.round(sampleRate / fxmax);
        this.tmax = Math.round(sampleRate / fxmin);
        this.bias = new float[tmax + 1];
        this.ac = new float[tmax + 1];

        double m = Math.log(fxlow);
        double B = m * m;
        double D = 1;
        double E = 0.95;
        double v = Math.log(fxhigh);
        double w = v * v;
        double o = 1;
        double x = 0.95;
        double G = (m + v) / 2;
        double F = G * G;
        double H = 1;
        double t = 1;
        double q = B * G * o + m * H * w + D * F * v - D * G * w - B * H * v - m * F * o;
        double y = E * G * o + m * H * x + D * t * v - D * G * x - E * H * v - m * t * o;
        double J = B * t * o + E * H * w + D * F * x - D * t * w - B * H * x - E * F * o;
        double I = B * G * x + m * t * w + E * F * v - E * G * w - B * t * v - m * F * x;

        for (int r = tmin - 1; r <= tmax; r++) {
            G = Math.log(sampleRate / (double) r);
            bias[r] = (float) ((G * G * y + G * J + I) / q);
        }
    }

    public FxResult calculateFx(float[] a, int len) {
        float energy = 0;
        for (int i = 0; i < len; i++) energy += a[i] * a[i];
        energy = (float) Math.sqrt(energy / len);

        for (int f = tmin - 1; f <= tmax; f++) {
            float S = 0, T = 0, R = 0;
            for (int j = 0; j < len - f; j++) {
                S += a[j] * a[j + f];
                T += a[j] * a[j];
                R += a[j + f] * a[j + f];
            }
            ac[f] = bias[f] * S / (float) Math.sqrt(T * R);
        }

        int N = tmin - 1;
        float max = ac[N];
        for (int f = tmin; f <= tmax; f++) {
            if (ac[f] > max) {
                max = ac[f];
                N = f;
            }
        }

        if (N <= tmin + 1 || N >= tmax - 1 || max < 0.5) {
            return new FxResult(0, N / (float) srate, max, energy);
        }

        float K = ac[N - 1];
        float P = ac[N];
        float L = ac[N + 1];
        float g = 0.5f * (L - K) / (2 * P - K - L);
        float h = N + g;

        if (max > 0.9f) {
            for (int b = 2; b > 1; b--) {
                boolean strong = true;
                for (int m = 1; m < b; m++) {
                    int c = Math.round((m * h) / b);
                    if (ac[c] < 0.9 * max) {
                        strong = false;
                        break;
                    }
                }
                if (strong) {
                    h = h / b;
                    break;
                }
            }
        }

        float fx = srate / h;
        return new FxResult(fx, h / srate, max, energy);
    }

    public static class FxResult {
        public final float fx, tx, vs, en;

        public FxResult(float fx, float tx, float vs, float en) {
            this.fx = fx;
            this.tx = tx;
            this.vs = vs;
            this.en = en;
        }
    }
}

