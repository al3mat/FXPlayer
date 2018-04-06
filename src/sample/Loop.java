package sample;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;


public class Loop 
{
	int songs, cycles, i, to_wait, current, first;
	boolean stop = false;
	MediaPlayer mp;


	void loopCall(int loop_state, MediaPlayer player)
	{
		if(loop_state == 1)
		{
//				this.mp = player;
				this.loop_start_single(player);
		}//prima era fatto con mp ma non funzionava lo stesso
		else
		{
			if(loop_state == 0)
			    this.stop_loop(player);
		}
	}



	void loop_start_single(MediaPlayer player)
	{
		System.out.println("nel loop di singola canzone");
		player.setCycleCount(AudioClip.INDEFINITE);
	}




	void stop_loop(MediaPlayer mp)
	{
		System.out.println("nella funzione stop_loop");

		stop = true;

		if(!mp.getCurrentTime().equals(mp.getTotalDuration()))
		{
			mp.setCycleCount(0);
		}//caso in cui si disattivi il loop mentre и ancora in riproduzione
	}//impostare che, una volta finita la riproduzione, venga riprodotto il brano successivo nella playlist
}

