package xyz.zedler.patrick.spelling.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.plattysoft.leonids.ParticleSystem;

import xyz.zedler.patrick.spelling.R;

public class ConfettiUtil {

    private final static float speedMin = 0.1f;
    private final static float speedMax = 0.5f;
    private final static float acceleration = 0.0001f;

    private final static int particlesMax = 30;
    private final static int particlesPerSecond = 100;
    private final static int emittingDuration = 500;
    private final static int livingDuration = 4000;
    private final static int fadeOutDuration = 1500;

    private final static int minAngle = 190;
    private final static int maxAngle = 350;

    public static void explode(ViewGroup parent, View emitter) {
        ParticleSystem psRedLeft = getParticleSystem(parent, R.color.retro_red_fg);
        psRedLeft.setSpeedModuleAndAngleRange(
                speedMin, speedMax, minAngle, 270
        );
        psRedLeft.emit(emitter, particlesPerSecond, emittingDuration);

        ParticleSystem psRedRight = getParticleSystem(parent, R.color.retro_red_fg);
        psRedRight.setSpeedModuleAndAngleRange(
                speedMin, speedMax, 270, maxAngle
        );
        psRedRight.emit(emitter, particlesPerSecond, emittingDuration);

        ParticleSystem psYellowLeft = getParticleSystem(parent, R.color.retro_yellow);
        psYellowLeft.setSpeedModuleAndAngleRange(
                speedMin, speedMax, minAngle, 270
        );
        psYellowLeft.emit(emitter, particlesPerSecond, emittingDuration);

        ParticleSystem psYellowRight = getParticleSystem(parent, R.color.retro_yellow);
        psYellowRight.setSpeedModuleAndAngleRange(
                speedMin, speedMax, 270, maxAngle
        );
        psYellowRight.emit(emitter, particlesPerSecond, emittingDuration);

        ParticleSystem psBlueLeft = getParticleSystem(parent, R.color.retro_blue);
        psBlueLeft.setSpeedModuleAndAngleRange(
                speedMin, speedMax, minAngle, 270
        );
        psBlueLeft.emit(emitter, particlesPerSecond, emittingDuration);

        ParticleSystem psBlueRight = getParticleSystem(parent, R.color.retro_blue);
        psBlueRight.setSpeedModuleAndAngleRange(
                speedMin, speedMax, 270, maxAngle
        );
        psBlueRight.emit(emitter, particlesPerSecond, emittingDuration);
    }

    private static ParticleSystem getParticleSystem(ViewGroup parent, int colorId) {
        Drawable drawable = ResourcesCompat.getDrawable(
                parent.getResources(),
                R.drawable.shape_pill,
                null
        );
        assert drawable != null;
        DrawableCompat.setTint(drawable, ContextCompat.getColor(parent.getContext(), colorId));
        ParticleSystem ps = new ParticleSystem(
                parent,
                particlesMax,
                drawable,
                livingDuration
        );
        ps.setInitialRotationRange(0, 360);
        ps.setRotationSpeedRange(90, 180);
        ps.setAcceleration(acceleration, 90);
        ps.setFadeOut(fadeOutDuration, new DecelerateInterpolator());
        return ps;
    }
}
