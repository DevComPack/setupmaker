package com.dcp.sm.gui.pivot.transitions;

import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.effects.FadeDecorator;
import org.apache.pivot.wtk.effects.Transition;
import org.apache.pivot.wtk.effects.TransitionListener;

/**
 * Fade transition class.
 */
public class AppearTransition extends Transition {
    private Component component;
    private FadeDecorator fadeDecorator = new FadeDecorator();

    public AppearTransition(Component component, int duration, int rate) {
        super(duration, rate, false);
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public void start(TransitionListener transitionListener) {
        component.getDecorators().add(fadeDecorator);

        super.start(transitionListener);
    }

    @Override
    public void stop() {
        component.getDecorators().remove(fadeDecorator);

        super.stop();
    }

    @Override
    protected void update() {
        fadeDecorator.setOpacity(getPercentComplete());
        component.repaint();
    }
}
