package be.encelade.bricks;

import org.joda.time.LocalDateTime;

public class LogicEngine {

    private GraphicEngine graphicEngine;

    //  heartBeat happens 10 times a sec. --> 10h in game per real world second
    private static final float timeRate = 0.1f;

    private static float summedTpf = 0;
    private static LocalDateTime date = new LocalDateTime(2100, 1, 1, 12, 0, 0);
    private static int currentDay = date.getDayOfMonth();

    public void setGraphicEngine(GraphicEngine graphicEngine) {
        this.graphicEngine = graphicEngine;
    }

    public void addTpf(float tpf) {
        summedTpf += tpf;
        if (summedTpf > timeRate) {
            heartBeat();
            summedTpf = tpf;
        }
    }

    public LocalDateTime getDate() {
        return date;
    }

    private void heartBeat() {
        date = date.plusHours(1);
        if (currentDay != date.dayOfMonth().get()) {
            currentDay = date.dayOfMonth().get();
            graphicEngine.updateDate(date);
        }
    }

}
