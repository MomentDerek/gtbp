package site.yuanshen.gtbp.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.yuanshen.gtbp.model.User;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 匹配池
 *
 * @author Moment
 */
@Data
@AllArgsConstructor
@Slf4j
public class MatchingWrap {

    private final static TreeMap<User, LocalTime> matchingSet = new TreeMap<>(Comparator.comparing(User::getScore));

    private final static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

    public static User getCompetitor(User user) {
        User competitor = getClosestUser(user);
        if (competitor == null) matchingSet.put(user, LocalTime.now());
        if (Math.abs(competitor.getScore() - user.getScore()) >= 500) {
            matchingSet.put(user, LocalTime.now());
            // 定时任务10s
            scheduledExecutorService.schedule(() -> callCompetitorFirstLevel(user), 10, TimeUnit.SECONDS);
        }
        // 分配对手
        return competitor;
    }

    private static void callCompetitorFirstLevel(User user) {
        User competitor = getClosestUser(user);
        if (competitor == null) scheduledExecutorService.schedule(() -> callCompetitorFirstLevel(user), 5, TimeUnit.SECONDS);
        if (Math.abs(competitor.getScore() - user.getScore()) >= 1000) {
            scheduledExecutorService.schedule(() -> callCompetitorSecondLevel(user), 10, TimeUnit.SECONDS);
        }
        // 分配对手
    }

    private static void callCompetitorSecondLevel(User user) {
        User competitor = getClosestUser(user);
        if (competitor == null) scheduledExecutorService.schedule(() -> callCompetitorSecondLevel(user), 5, TimeUnit.SECONDS);
        // 分配对手
    }

    private static User getClosestUser(User user) {
        synchronized (matchingSet) {
            User higher = matchingSet.higherKey(user);
            User lower = matchingSet.lowerKey(user);
            //判空
            if (higher == null) {
                if (lower != null) {
                    matchingSet.remove(lower);
                    return lower;
                }
                return null;
            }
            if (lower == null) {
                matchingSet.remove(higher);
                return higher;
            }
            //判断哪个距离更短
            boolean isHigher = higher.getScore() - user.getScore() - (user.getScore() - lower.getScore()) > 0;
            matchingSet.remove(isHigher ? higher : lower);
            return isHigher ? higher : lower;
        }
    }
}
