package sample;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;


public class Loop 
{
	int songs, cycles, i, to_wait, current, first;
	boolean stop = false;
	MediaPlayer mp;
	
	
	
	
	void loop_selection()
	{
		if(songs == 1)
			this.loop_start_single(mp);
		else
			this.loop_start_playlist();
	}

	
	void loop_start_single(MediaPlayer mp)
	{
		mp.setCycleCount(AudioClip.INDEFINITE);			
	}


	int loop_start_playlist()
	{
		System.out.println("nel metodo della playlist");
		
		while(!stop)
		{				
			if(current == songs)
				return first;
		}
		return current;
	}
//se non si riesce a spostare di qui il loop della playlist, eliminare la selezione e questo metodo


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
		}//caso in cui si disattivi il loop mentre и ancora in riproduzione
	}//impostare che, una volta finita la riproduzione, venga riprodotto il brano successivo nella playlist
}

