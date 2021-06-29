package com.edu.monash.fit3077.viewModel;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestStatus;
import com.edu.monash.fit3077.service.MyResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * ViewModel for the count down timer of bid request.
 * The count down timer is implemented using the timer provided by Android
 */
public class BidTimerViewModel extends ViewModel {
    // the timer object
    private CountDownTimer timer;
    // the current value of the timer
    private MutableLiveData<MyResponse<String>> timerCurrentValue;
    // a boolean indicating whether the timer has finished
    private MutableLiveData<Boolean> timerEnd;

    public BidTimerViewModel(){
        timerCurrentValue = new MutableLiveData<>();
        timerEnd = new MutableLiveData<>(false);
    }

    /**
     * This function determines the start time of the count down timer and starts the timer
     * @param bidRequest
     */
    public void startTimer(BidRequest bidRequest) {
        long timerStartTime = Duration.between(Instant.now(), bidRequest.getValidUntil()).toMillis();

        // cancel the previous timer (if any)
        if (timer != null) {
            timer.cancel();
        }

        // starting from the remaining duration, counting down second by second
        timer = new CountDownTimer(timerStartTime, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                String timerVal = days + " day(s), " + hours + ":" + minutes + ":" + seconds;

                timerCurrentValue.setValue(MyResponse.successResponse(timerVal));
            }

            @Override
            public void onFinish() {
                timerCurrentValue.setValue(MyResponse.successResponse("Overdue"));
                timerEnd.setValue(true);
            }
        };

        // Only start the timer for Alive bid, else show - for Closed Down Bid
        if (bidRequest.getStatus() == BidRequestStatus.ALIVE) {
            timer.start();
        } else {
            timerCurrentValue.setValue(MyResponse.successResponse("-"));
        }

    }

    // get the current count down time
    public LiveData<MyResponse<String>> getTimerCurrentValue() {
        return timerCurrentValue;
    }

    // notify the views when timer ends, i.e. when the bid request time out
    public LiveData<Boolean> getTimerEndStatus() { return timerEnd; }

    public void stopTimer() {
        timer.cancel();
    }

}
