import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.Port;
import lejos.hardware.port.*;
import lejos.hardware.Brick;
import lejos.hardware.Sound;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.hardware.sensor.*;
import lejos.hardware.Button;
import java.util.*;

/**
 * Dato:		25.09.2018
 * Forfattere:	Torstein H. Sundfær, Cato Bakken, Torbjørn B. Lauvvik, Jonas B. Jacobsen, Eirik Hemstad
 *
 * Historie:
 * ---------
 *
 * 26.09.2018_05 - version a_0.5
 * - Tried to fix sensor error
 *
 * 26.09.2018_04 - Version a_0.4
 * - Added second color sensor
 *
 * 26.09.2018_03
 * Version a_0.3
 * - Fixed issue with robot running backwards
 *
 * 26.09.2018_02
 * Version a_0.2
 * - Fixed Color Sensor mode error
 *
 * 26.09.2018_01
 * Versjon a_0.1
 *
 * 25.09.2018
 * Første revisjon
 *
**/

class IDIot {
	// Oppsett av EV3 Objekter
	private static EV3 ev3;
	private static Brick brick;
	private static Port p1;
	private static Port p2;
	private static TextLCD lcd;
	private static NXTColorSensor colorSensorRight; //fargesensor høyre ny type
	//private static ColorSensor colorSensorRight2;
	private static EV3ColorSensor colorSensorLeft; //fargesensor vesntre gammel type
	private static SensorMode colorLeft;
	private static SensorMode colorRight;
	private static float[] colorSampleLeft;
	private static float[] colorSampleRight;
	//private static int vol = Sound.getVolume();

	// Konstanter
	private static final int SPEED = 450;
	private static final int TURN_SPEED = 200;
	private static final int FLAGG_SPEED = 250;

	// Andre variabler
	private static final String VERSION = "a_0.6.3";
	//lyd
	private static long naaTid;
	private static long forrigeTid;
	public static void main(String[] args) {
		// Print startmelding
		System.out.println("IDIot versjon " + VERSION);

		// Oppsett av robotens komponenter
		brick = BrickFinder.getDefault();
		p1 = brick.getPort("S1"); // Fargesensor venstre
		p2 = brick.getPort("S2"); // Fargesensor høyre

		// Venstre sensor = port 1
		colorSensorLeft = new EV3ColorSensor(p1);
		// Høyre sensor = port 2
		//colorSensorRight = new NXTColorSensor(SensorPort.S2);
		//colorSensorRight2 = new ColorSensor(p2);

		colorLeft = colorSensorLeft.getColorIDMode();
		//colorRight = colorSensorRight.getColorIDMode();

		// Hastighet på roboten
		Motor.A.setSpeed(SPEED);		// Venstre
		Motor.B.setSpeed(SPEED);		// Høyre
		Motor.D.setSpeed(FLAGG_SPEED);	// Flagg

		// Andre variabler
		//Sound.setVolume(66);

		// Start av programmet
		int direction = 0;

		while (true) {
			direction = driveUntilBlack();
			turnUntilWhite(direction);
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

		startMotor(false);

		int result = 0;
		while (true) {
			result = checkForBlack();
			if (result != 0) {
				break;
			}
		}

		return result;
	}

	private static void turnUntilWhite(int direction) {
		// Sving inntil begge er utenfor svart
		if(direction==1){
			//TODO test om det er bedre å senke farten på det ene hjulet istedet for å stoppe det.
			Motor.A.stop();
			Motor.B.setSpeed(TURN_SPEED);
		}
		else{
			Motor.B.stop();
			Motor.A.setSpeed(TURN_SPEED);
		}
		while (true) {
			int result = checkForBlack();
			if (result == 0) {
				break;
			}
		}
	}

	// Check for black
	// = 0: none
	// = 1: left
	// = 2: right
	// = 3: both
	private static int checkForBlack() {
		colorSampleLeft = new float[colorLeft.sampleSize()];
		colorLeft.fetchSample(colorSampleLeft, 0);
		if((int)colorSampleLeft[0] == 7) {
			return 1;
		}
		/*colorSampleRight = new float[colorRight.sampleSize()];
		colorRight.fetchSample(colorSampleRight, 0);*/

		/*if (colorSensorRight.getColorID() == 7) {
			return 2;
		}*/

		/*if (colorSensorRight2.getColorID() == 7) {
			return 2;
		}*/

		return 0;
	}
}
