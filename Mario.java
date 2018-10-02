import lejos.hardware.Sound;
public class Mario {
	public static long naaTid;
	public static long forrigeTid;
	public static int lydTeller = 0;
	public static int lydRunde;
	public static int[][] lyd = new int[][]{{659,100,150},{659,100,300},{659,100,300},{523,100,100},{659,100,300},{784,100,550},{392,100,575},{523, 100, 450},{392, 100, 400},{330, 100, 500},{440, 100, 300},{494, 80, 330},{466, 100, 150},{440, 100, 300},{392, 100, 200},{659, 80, 200},{784, 50, 150},{880, 100, 300},{698, 80, 150},{784, 50, 350},{659, 80, 300},{523, 80, 150},{587, 80, 150},{494, 80, 500},{523, 100, 450},{392, 100, 400},{330, 100, 500},{440, 100, 300},{494, 80, 330},{466, 100, 150},{440, 100, 300},{392, 100, 200},{659, 80, 200},{784, 50, 150},{880, 100, 300},{698, 80, 150},{784, 50, 350},{659, 80, 300},{523, 80, 150},{587, 80, 150},{494, 80, 1000}};
	private static int vol = Sound.getVolume();


	public static void play() {
		System.out.println("Mario!");
		Sound.setVolume(66);

		int freq = lyd[lydTeller][0];
		int dur = lyd[lydTeller][1];
		int delay = lyd[lydTeller][2];
		naaTid = java.lang.System.currentTimeMillis();
		if ((naaTid-forrigeTid)<delay){
			//ingenting
		}
		else if (100 <= freq  && freq  <= 12000 &&
			10  <= dur   && dur   <= 10000) {
			Sound.playTone(freq, dur);
			forrigeTid = naaTid;
			lydRunde++;
			if(lydTeller==(lyd.length-1)){
				lydTeller=0;
			}
			else{
				lydTeller++;
			}
		}
	}
}