package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@TeleOp(name = "Mecanum")
//@Disabled
public class Mecanum extends LinearOpMode {
    //private DistanceSensor sensorDistance;
    private Limelight3A limelight;
    private double tx, ty, ta, tv; // Vision variables
    private double targetAlignThreshold = 10.0;
    private double moveSpeed = 0.384;//cancels out turtle mode and slow turning to ultimately be .2
    private DcMotor backRightMotor, backLeftMotor, frontRightMotor, frontLeftMotor;

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
        int rumbleNum = 1; // Used later for the speed setting rumble

        // Set the bumpers that are used later all to false
        boolean g1RightBumperPressed = false; // Toggles between T and F when bumper is clicked
        boolean g1RightBumperPrevious = false; // Temporary storage for conditionals

        boolean g2RightBumperPressed = false; // Toggles between T and F when bumper is clicked
        boolean g2RightBumperPrevious = false; // Temporary storage for conditionals

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


            boolean g1RightBumperInput = gamepad1.right_bumper; // Actual state of bumper
            boolean g2RightBumperInput = gamepad2.right_bumper; // Actual state of bumper

            if (y == 0 || x == 0)
                rx *= 0.6; // Puts motor power at 80% when the robot is turning-in-place

            //Speed setting is toggled between slow and fast with one click of the right bumper
            if (g1RightBumperInput && !g1RightBumperPrevious) { //If the right bumper is TRUE (pressed) AND it was previously FALSE (Not Pressed)
                g1RightBumperPressed = !g1RightBumperPressed; // Toggle the boolean for the bumper to the opposite, T to F or F to T
                /* .rumbleBlips() rumbles the number of times you
                put into the (), since rumble num is ++ every press
                of the bumper, we check if it is even or odd to
                trigger the according number of blips. %2 always
                returns either 0 or 1, so if you add 1, you can
                switch between 1 blip or 2 blips
                */
                gamepad1.rumbleBlips(rumbleNum % 2 + 1);
                rumbleNum++;
            }

            g1RightBumperPrevious = g1RightBumperInput; // Set the new previous state of the bumper

            if (g1RightBumperPressed) powerMultiplier = 0.6; // Set motor power to 60%
            if (!g1RightBumperPressed) powerMultiplier = 1.0; // Set motor power to 100%

            // Right bumper on gamepad 2 for open/close claw
            if (g2RightBumperInput && !g2RightBumperPrevious) { // Refer to GamePad 1's conditional above
                g2RightBumperPressed = !g2RightBumperPressed;
            }

            g2RightBumperPrevious = g2RightBumperInput; // Update previous

            //Code for claw
            if (g2RightBumperPressed) claw.setPosition(0.4); // Open Claw Position
            if (!g2RightBumperPressed) claw.setPosition(0.17); // Closed Claw Position

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
}
