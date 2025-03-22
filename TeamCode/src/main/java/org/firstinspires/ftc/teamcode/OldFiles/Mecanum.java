package org.firstinspires.ftc.teamcode.OldFiles;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.*;


@TeleOp(name = "Mecanum")
@Disabled
public class Mecanum extends LinearOpMode {
    //private DistanceSensor sensorDistance;
    private Limelight3A limelight;
    private double tx, ty, ta, tv; // Vision variables
    private double targetAlignThreshold = 10.0;
    private double moveSpeed = 0.384;//cancels out turtle mode and slow turning to ultimately be .2
    private DcMotor backRightMotor, backLeftMotor, frontRightMotor, frontLeftMotor;

    // Declare two maps to track the toggle states and previous states of buttons.
    // The keys are button names (String), and the values are their states (Boolean).
    private final Map<String, Boolean> toggleStates = new HashMap<>();
    private final Map<String, Boolean> previousStates = new HashMap<>();
    int rumbleNum = 1; // Used later for the speed setting rumble


    @Override
    public void runOpMode() throws InterruptedException {
        //Setting up limelight and giving it a name
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        //Setting limelight to pipeline 5 which we set up in the limelight software. Pipeline 5 is the neural detector
        limelight.pipelineSwitch(5);
        //Setting up the motors in the code based on the configuration names of the motors
        backRightMotor = hardwareMap.dcMotor.get("backRight");// Port 0
        backLeftMotor = hardwareMap.dcMotor.get("backLeft"); // Port 1
        frontRightMotor = hardwareMap.dcMotor.get("frontRight"); // Port 2
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeft"); // Port 3

        Servo claw = hardwareMap.servo.get("clawServo"); // Port 0
        Servo wrist = hardwareMap.servo.get("wristServo"); // Port 1

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart(); // Don't run code until start button is pressed on DS

        if (isStopRequested()) return; // To Stop the opMode when stop button is pressed on DS

        limelight.start(); //Activate Limelight

        double wristPos=.5; //Used later for setting the position of the wrist
        double powerMultiplier = 1.0; // Used later for the speed setting



        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult(); //Get the latest data from limelight

            if (result != null) { // If the limelight isn't null (aka it sees a sample)
                tv = result.getTyNC();
                if (tv != 0) tv = 1; //tv is 0 if no samples are seen, 1 if samples are seen
                else tv = 0;
                tx = result.getTx(); // Horizontal offset
                ty = result.getTy(); // Vertical offset
                ta = result.getTa(); // Target area
            } else { //default to 0 to prevent unwanted movement
                tv = 0.0;
                tx = 0.0;
                ty = 0.0;
                ta = 0.0;
            }

            if(tv == 1.0) { // gamepad1 vibrates continuously when a sample is detected
                gamepad1.rumbleBlips(1);
            }

            // Code to let driver2 make small and slow movements of the drivetrain
            if (gamepad2.dpad_down) moveRobot(.5,0,0);
            if (gamepad2.dpad_up) moveRobot(-.5,0,0);
            if (gamepad2.dpad_right) { // Strafe right
                moveRobot(0,-.5,0);
            }
            if (gamepad2.dpad_left) { //Strafe Left
                moveRobot(0,.5,0);
            }
            if (gamepad1.right_trigger > 0.5) {
                if (tv == 1.0) { // if at least 1 sample is seen
                    alignWithTarget(); // Align with target
                } 
            }
            double y = -gamepad1.left_stick_y; // REV Gamepad left joystick, so Y is up-down motion
            double x = gamepad1.left_stick_x * 1.6; // REV Gamepad left joystick, so X is left-right motion
            double rx = gamepad1.right_stick_x;  // REV Gamepad right joystick, so x is right-left motion



            if (y == 0 || x == 0)
                rx *= 0.6; // Puts motor power at 80% when the robot is turning-in-place


            if(toggleButton(gamepad1.right_bumper, "g1RightBumper")) powerMultiplier=0.6;
            else powerMultiplier=1.0;


            //Code for claw
            if (toggleButton(gamepad2.right_bumper, "g2RightBumper")) claw.setPosition(0.4); // Open Claw Position
            else claw.setPosition(0.17); // Closed Claw Position

            //Code for wrist
            if (gamepad2.x) wristPos = .25;// Right Position
            if (gamepad2.y) wristPos = .5;// Center Position
            if (gamepad2.b) wristPos = .75;// Left Position
            wrist.setPosition(wristPos); //move the wrist to the most recent request

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double backLeftPower = (y - x + rx) / denominator;
            double frontLeftPower = (x + y + rx) / denominator;
            double backRightPower = (x + y - rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;

            // Implementing the power Multiplier from above
            frontLeftMotor.setPower(frontLeftPower * powerMultiplier);
            frontRightMotor.setPower(frontRightPower * powerMultiplier);
            backLeftMotor.setPower(backLeftPower * powerMultiplier);
            backRightMotor.setPower(backRightPower * powerMultiplier);

            //Some useful telemetry data to have on the DS

            telemetry.addData("SampleFound?:", tv); // tv is 0 if not sample seen, 1 if one or more samples are seen

            telemetry.addData("Speed: ", powerMultiplier); // Shown on DS for reference
            telemetry.addData("\nFront Left Motor ", frontLeftPower * powerMultiplier * 100);
            telemetry.addData("Front Right Motor ", frontRightPower * powerMultiplier * 100);
            telemetry.addData("Back Left Motor ", backLeftPower * powerMultiplier * 100);
            telemetry.addData("Back Right Motor ", backRightPower * powerMultiplier * 100);

            telemetry.addData("\nServo Position: ", claw.getPosition());

            //Print joystick values
            telemetry.addData("\nG1 Left Stick X", x);
            telemetry.addData("G1 Left Stick Y", y);
            telemetry.addData("G1 Right Stick X", rx);

            telemetry.update(); // Update all the data on the DS telemetry window
        }
        limelight.stop();
    }
    private void moveRobot(double x, double y, double yaw) { // to optimize and shorten robot movement in the code
        x = -x;
        double leftFrontPower = x - y - yaw;
        double rightFrontPower = x + y + yaw;
        double leftBackPower = x + y - yaw;
        double rightBackPower = x - y + yaw;

        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        frontLeftMotor.setPower(leftFrontPower);
        frontRightMotor.setPower(rightFrontPower);
        backLeftMotor.setPower(leftBackPower);
        backRightMotor.setPower(rightBackPower);
    }

    private void alignWithTarget() {
        if (tx > targetAlignThreshold) {
            robotDrive(moveSpeed, -moveSpeed); // Turn right
        } else if (tx < -targetAlignThreshold) {
            robotDrive(-moveSpeed, moveSpeed); // Turn left
        } else {
            robotDrive(-moveSpeed, -moveSpeed);
        }
    }
    private void robotDrive(double leftPower, double rightPower) {
        frontLeftMotor.setPower(leftPower);
        frontRightMotor.setPower(rightPower);
        backLeftMotor.setPower(leftPower);
        backRightMotor.setPower(rightPower);
    }

    private void stopRobot() {
        robotDrive(0, 0); // Stop all motion
    }
    private boolean toggleButton(boolean buttonInput, String buttonName) {
        // Get the previous state for this button, default to false if not set
        boolean previousState = previousStates.getOrDefault(buttonName, false);// Fetch the previous state; default is false
        boolean toggleState = toggleStates.getOrDefault(buttonName, false);// Fetch the current toggle state; default is false

        // Check for toggle
        if (buttonInput && !previousState) {
            toggleState = !toggleState; // Toggle the state
            toggleStates.put(buttonName, toggleState); // Update toggle state in map

            // Vibrate if specific button is pressed
            if(buttonName.equals("g1RightBumper")) {
                gamepad1.rumbleBlips(rumbleNum % 2 + 1);
                rumbleNum++;
            }
        }

        // Update the previous state in the map
        previousStates.put(buttonName, buttonInput);

        // Return the current toggle state for this button
        return toggleState;
    }
}
