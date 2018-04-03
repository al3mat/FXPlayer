package sample;

import java.util.List;

import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.MediaPlayer;

public class Equalizer 
{
	MediaPlayer mp;
	int i = 0, nBands = 7;
	EqualizerBand frequency;
	List<EqualizerBand> bands;
	boolean enable = false;							//collegare al pulsante di attivazione: al metodo che ne gestisce l'evento
	double gain, mid, scale, freq = 0;
	
	
	
	void getBands()
	{

		
//		mp.getAudioEqualizer().MAX_NUM_BANDS;   controllare come inserire il numero massimo di linee di banda
		bands = mp.getAudioEqualizer().getBands();
		
		mp.getAudioEqualizer().setEnabled(enable);
		
		bands.clear();
		
		for(i = 0; i < bands.size(); i++)
		{
			scale = 0.4*(1+Math.cos((i/nBands-1)*(2*Math.PI)));
			mid = (EqualizerBand.MAX_GAIN + EqualizerBand.MIN_GAIN)/2;
			gain = EqualizerBand.MIN_GAIN + mid + (mid*scale);
			frequency.gainProperty().set(gain);
//			bands.set(i, frequency);
			bands.add(new EqualizerBand(freq, freq/2, gain));
			
			switch(i)
			{
			case 0: freq = 60; break;
			case 1: freq = 250; break;
			case 2: freq = 500; break;
			case 3: freq = 2000; break;
			case 4: freq = 4000; break;
			case 5: freq = 6000; break;
			case 6: freq = 20000; break;
			}//spero imposti la massima frequenza della banda corrente->controllare
		}//in teoria calcola le bande di frequenza
	}

	
	void setFrequence(int current)
	{
		bands.get(current).setBandwidth(0);
	}//collegare gli slider alla singola banda, indicata da current

}
