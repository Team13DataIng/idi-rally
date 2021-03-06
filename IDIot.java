import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.HiTechnicColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.port.*;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;
import lejos.hardware.Button;
import java.util.*;

/**
 * Dato:		25.09.2018
 * Forfattere:	Torstein H. Sundfær, Cato Bakken, Torbjørn B. Lauvvik, Jonas B. Jacobsen, Eirik Hemstad
**/

class IDIot {
	// Oppsett av EV3 Objekter
	private static EV3 ev3;
	private static Brick brick;
	private static Port p1;
	private static Port p2;
	private static Port p4;
	private static TextLCD lcd;
	private static HiTechnicColorSensor colorSensorRight; // Høyre fargesensor
	private static EV3ColorSensor colorSensorLeft; // Venstre fargesensor
	private static SensorMode colorLeft;
	private static SensorMode colorRight;
	private static float[] colorSampleLeft;
	private static float[] colorSampleRight;
	private static SampleProvider touchSensor;
	private static float[] touchSample;

	// Konstanter
	private static final int SPEED = 320;				// Stabil fart: 320
	private static final int TURN_SPEED = 300;			// Stabil fart: 300
	private static final int SWORD_SPEED = 250;
	private static final int FLAGG_SPEED = 250;
	private static final int REVERSE_SPEED = 80;

	//Variabel som bestemmer om roboten skal kjøre eller er ferdig
	private static boolean go = true;

	// Andre variabler
	private static final String VERSION = "b_1.5.2";
	private static float blackLimitLeft;
	private static float blackLimitRight;

	public static void main(String[] args) {
		// Print startmelding
		System.out.println("IDIot versjon " + VERSION);

		// Oppsett av robotens komponenter
		brick = BrickFinder.getDefault();
		p1 = brick.getPort("S1"); // Fargesensor venstre
		p2 = brick.getPort("S2"); // Fargesensor høyre
		p4 = brick.getPort("S4"); // Trykksensor

		// Venstre sensor = port 1
		colorSensorLeft = new EV3ColorSensor(p1);
		// Høyre sensor = port 2
		colorSensorRight = new HiTechnicColorSensor(p2);

		// Sett opp sensorer slik at de går på RGB
		colorLeft = colorSensorLeft.getMode("RGB");
		colorRight = colorSensorRight.getMode("RGB");

		// Skann hvitt ved start av programmet slik at sensorene har en referanse
		colorSampleRight = new float[colorRight.sampleSize()];
		colorRight.fetchSample(colorSampleRight, 0);
		blackLimitRight = colorSampleRight[0]/2;

		colorSampleLeft = new float[colorLeft.sampleSize()];
		colorLeft.fetchSample(colorSampleLeft, 0);
		blackLimitLeft = colorSampleLeft[0]/2;

		// Sett opp trykksensor
		touchSensor = new EV3TouchSensor(p4);

		// Hastighet på roboten
		Motor.A.setSpeed(SPEED);		// Venstre
		Motor.B.setSpeed(SPEED);		// Høyre
		Motor.C.setSpeed(SWORD_SPEED);	// Sverd
		Motor.D.setSpeed(FLAGG_SPEED);	// Flagg

		// Starter flagg og sverd
		Motor.C.forward();				// Sverd
		Motor.D.forward();				// Flagg

		// Start av programmet
		int direction = 0;

		while (go) {
			// Sjekk sensor på hver side, og juster retningen den skal kjøre
			direction = driveUntilBlack();
			if(direction != 0) turnUntilWhite(direction);
		}
	}

	private static void startMotor(boolean reverse) {
		// Start begge motorer
		if (!reverse) {
			Motor.A.forward();
			Motor.B.forward();
		} // Dersom reverse = true, går motorene bakover
		else {
			Motor.A.backward();
			Motor.B.backward();
		}
	}

	private static void stopMotor(int duration) {
		// Stopp begge motorer
		// Stopp for duration dersom duration ikke er lik 0
		if (duration < 0)
			throw new IllegalArgumentException("Duration cannot be lower than 0.");

		try {
			Thread.sleep(duration);
		}
		catch(InterruptedException e) {
			System.out.println("Thread 1 interrupted.");
		}
	}

	private static int driveUntilBlack() {
		// Kjør inntil en av sensorene registrerer svart
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);

		int result = 0;
		while (true) {
			if (checkForTouch()) {
				go = false;
				break;
			}
			//mario.play();
			result = checkForBlack();
			if (result != 0) {
				break;
			}
			startMotor(false);
		}

		return result;
	}

	private static void turnUntilWhite(int direction) {
		// Sving inntil begge er utenfor svart
		if(direction == 1){
			//System.out.println("Svinger til venstre");
			Motor.A.stop();
			Motor.B.setSpeed(TURN_SPEED);
		}
		else if(direction == 2){
			//System.out.println("Svinger til høyre");
			Motor.B.stop();
			Motor.A.setSpeed(TURN_SPEED);
		}
		//long naaTid = java.lang.System.currentTimeMillis();
		while (true) {
			if (checkForTouch()) {
				go = false;
				break;
			}
			//mario.play();
			int result = checkForBlack();
			if (result == 3) {
				// Dersom begge sensorene registrerer svart, skal den svinge andre veien
				if(direction == 2) {
					Motor.A.stop();
					Motor.B.setSpeed(TURN_SPEED);
					Motor.B.forward();
					try{
						Thread.sleep(600);
						System.out.println("Tråd sover 1s");
					}
					catch(InterruptedException e) {
						System.out.println("Feil elns");
					}
				}
				else if(direction == 1){
					Motor.B.stop();
					Motor.A.setSpeed(TURN_SPEED);
					Motor.A.forward();
					try{
						Thread.sleep(600);
						System.out.println("Tråd sover 1s");
					}
					catch(InterruptedException e) {
						System.out.println("Feil elns");
					}
				}
			}
			else if (result == 0) {
				break;
			}
		}
	}

	// Check for black
	// = 0: none
	// = 1: left
	// = 2: right
	private static int checkForBlack() {
		int status = 0;
		colorSampleLeft = new float[colorLeft.sampleSize()];
		colorLeft.fetchSample(colorSampleLeft, 0);
		colorSampleRight = new float[colorRight.sampleSize()];
		colorRight.fetchSample(colorSampleRight, 0);
		if(colorSampleLeft[0] <= blackLimitLeft) {
			// Svart på venstre
			status = 1;
		}

		if (colorSampleRight[0] <= blackLimitRight) {
			// Svart på høyre
			status = 2;
		}

		if (colorSampleRight[0] <= blackLimitRight && colorSampleLeft[0] <= blackLimitLeft ){
			System.out.println("Svart begge.");
			status = 3;
		}

		return status;
	}

	private static int testBlack() {
		colorSampleRight = new float[colorRight.sampleSize()];
		colorRight.fetchSample(colorSampleRight, 0);
		try{
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {
			System.out.println("Feil elns");
		}
		return 0;
	}

	private static boolean checkForTouch() {
		touchSample = new float[touchSensor.sampleSize()];

		if (touchSample != null && touchSample.length > 0) {
			touchSensor.fetchSample(touchSample, 0);
			if (touchSample[0] > 0) {
				return true;
			}
		}
		return false;
	}
}
