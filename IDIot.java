import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.Port;
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
 * 26.09.2018_01
 * Versjon 0.1
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
	private static EV3ColorSensor colorSensorLeft; //fargesensor vesntre gammel type
	private static SampleProvider colorReader;
	private static float[] colorSample;

	// Konstanter
	private static final int SPEED = 450;
	private static final int TURN_SPEED = 200;

	// Andre variabler
	private static final String VERSION = "a_0.1";

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
		colorSensorRight = new NXTColorSensor(p2);

		// Hastighet på roboten
		Motor.A.setSpeed(SPEED);		// Venstre
		Motor.B.setSpeed(SPEED);		// Høyre

		// Andre variabler


		int direction = 0;
		// Start av programmet
		while (true) {
			direction = driveUntilBlack();
			turnUntilWhite(direction);
		}
	}

	private static void startMotor(boolean reverse) {
		// Start begge motorer
		if (!reverse) {
			Motor.A.backward();
			Motor.B.backward();
		} // Dersom reverse = true, går motorene bakover
		else {
			Motor.A.forward();
			Motor.B.forward();
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
	// = 1: right
	// = 2: left
	// = 3: both
	private static int checkForBlack() {
		if (colorSensorRight.getColorID() == 7) {
			return 1;
		}
		if(colorSensorLeft.getColorID() == 7) {
			return 2;
		}
		else {
			return 0;
		}
	}
}
