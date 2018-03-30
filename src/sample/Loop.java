package sample;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;

public class Loop 
{
	int songs, cycles, i, to_wait;
	Playlist p;
	boolean stop = false;

	void loop_start(MediaPlayer mp)
	{
		
		if(songs == 1)
		{
			mp.setCycleCount(AudioClip.INDEFINITE);			
		}
		else
		{
			while(!stop)//mp.getCurrentCount() != mp.getCycleCount())
			{
				/*				
				if(!loop_state)
				{
					this.stop_loop(mp);
					break;
				}
				else
				{
				 */				
				for(i = 0; i < p.pl.size(); i++)
				 {
					 p.currentSong(i).play();
					 to_wait = (int) p.currentSong(i).getTotalDuration().toSeconds();

					 try 
					 {
						 wait(to_wait);
					 } 
					 catch (InterruptedException e) 
					 {
						 e.printStackTrace();
					 }
				 }
			}//riproduce una volta tutta la playlist
		}
	}



	void stop_loop(MediaPlayer mp)
	{

		System.out.println("nella funzione stop_loop");

		stop = true;

		if(!mp.getCurrentTime().equals(mp.getTotalDuration()))//controllare se arriva ad essere uguale
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

