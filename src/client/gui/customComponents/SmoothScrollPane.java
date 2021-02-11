package client.gui.customComponents;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/**
 * ScrollPane with  smooth transition scrolling.
 *
 * @author Matt, modified by Tobaeas10
 */
public class SmoothScrollPane extends ScrollPane {
    private final static int TRANSITION_DURATION = 200;

    //Default = 0.1
    private final static double BASE_MODIFIER = 0.1;

    /**
     * @param content
     *            Item to be wrapped in the scrollPane.
     */
    public SmoothScrollPane(Node content) {
        // ease-of-access for inner class
        ScrollPane scroll = this;

        EventHandler<ScrollEvent> handler = new EventHandler<ScrollEvent>() {
            private SmoothTransition transition;

            @Override
            public void handle(ScrollEvent event) {
                //Main modification start
                double dY = BASE_MODIFIER * scroll.getContent().getBoundsInLocal().getHeight()
                        * (scroll.getHeight() / scroll.getContent().getBoundsInLocal().getHeight());
                if(event.getDeltaY() < 0) {
                    dY = - dY;
                }
                final double deltaY = dY;
                //Main modification end

                final double width = scroll.getContent().getBoundsInLocal().getWidth();
                final double vValue = scroll.getVvalue();
                Interpolator interp = Interpolator.LINEAR;
                transition = new SmoothTransition(transition, deltaY) {
                    @Override
                    protected void interpolate(double frac) {
                        scroll.setVvalue(interp.interpolate(vValue, vValue - deltaY * getMod() / width, frac));
                    }
                };
                transition.play();
            }
        };

        content.setOnScroll(handler);
    }

    /**
     * @param t
     *            Transition to check.
     * @return {@code true} if transition is playing.
     */
    private static boolean playing(Transition t) {
        return t.getStatus() == Status.RUNNING;
    }

    /**
     * @param d1
     *            Value 1
     * @param d2
     *            Value 2.
     * @return {@code true} if values signs are matching.
     */
    private static boolean sameSign(double d1, double d2) {
        return (d1 > 0 && d2 > 0) || (d1 < 0 && d2 < 0);
    }

    /**
     * Transition with varying speed based on previously existing transitions.
     *
     * @author Matt
     */
    abstract static class SmoothTransition extends Transition {
        private final double mod;
        private final double delta;

        public SmoothTransition(SmoothTransition old, double delta) {
            setCycleDuration(Duration.millis(TRANSITION_DURATION));
            setCycleCount(0);
            /*
             if the last transition was moving in the same direction, and is still playing
             then increment the modifier. This will boost the distance, thus looking faster
             and seemingly consecutive.
            */
            if (old != null && sameSign(delta, old.delta) && playing(old)) {
                mod = old.getMod() + 1;
            } else {
                mod = 1;
            }
            this.delta = delta;
        }

        public double getMod() {
            return mod;
        }

        @Override
        public void play() {
            super.play();
            /*
             Even with a linear interpolation, startup is visibly slower than the middle.
             So skip a small bit of the animation to keep up with the speed of prior
             animation. The value of 10 works and isn't noticeable unless you really pay
             close attention. This works best on linear but also is decent for others.
            */
            if (getMod() > 1) {
                jumpTo(getCycleDuration().divide(10));
            }
        }
    }
}