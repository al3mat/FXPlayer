package sample;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;


public class Loop 
{
	int songs, cycles, i, to_wait, current, first;
	boolean stop = false;
	MediaPlayer mp;


	boolean loopCall(boolean loop_state, MediaPlayer player)
	{
		if(loop_state)
		{
			if(songs == 1)
			{
				this.mp = player;
				this.loop_start_single(mp);
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			this.stop_loop(player);
			return false;
		}

	}



	void loop_start_single(MediaPlayer mp)
	{
		mp.setCycleCount(AudioClip.INDEFINITE);			
	}

	
	
	void stop_loop(MediaPlayer mp)
	{

		System.out.println("nella funzione stop_loop");

		stop = true;

		if(!mp.getCurrentTime().equals(mp.getTotalDuration()))
		{
			mp.setCycleCount(0);

			try 
			{
				this.wait();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}//sostituire con il wait(long) nel quale mettere il tempo rimanente
		}//caso in cui si disattivi il loop mentre è ancora in riproduzione
	}//impostare che, una volta finita la riproduzione, venga riprodotto il brano successivo nella playlist
}

