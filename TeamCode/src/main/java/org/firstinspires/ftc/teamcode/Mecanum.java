//test test 1 2 3

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
//@Disabled
public class Mecanum extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        //Setting up the motors in the code based on the configuration names of the motors
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRight");// Port 0
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeft"); // Port 1
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRight"); // Port 2
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeft"); // Port 3

        Servo claw = hardwareMap.servo.get("clawServo"); // Port 0

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart(); // Don't run code until start button is pressed on DS

        if (isStopRequested()) return; // To Stop the opMode when stop button is pressed on DS

        double powerMultiplier = 1.0; // Used later for the speed setting

        // Set the bumpers that are used later all to false
        boolean g1RightBumperPressed = false; // Toggles between T and F when bumper is clicked
        boolean g1RightBumperPrevious = false; // Temporary storage for conditionals

        boolean g2RightBumperPressed = false; // Toggles between T and F when bumper is clicked
        boolean g2RightBumperPrevious = false; // Temporary storage for conditionals

        int rumbleNum = 1;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // REV Gamepad left joystick, so Y is up-down motion
            double x = gamepad1.left_stick_x * 1.6; // REV Gamepad left joystick, so X is left-right motion
            double rx = gamepad1.right_stick_x;  // REV Gamepad right joystick, so x is right-left motion


            boolean g1RightBumperInput = gamepad1.right_bumper; // Actual state of bumper
            boolean g2RightBumperInput = gamepad2.right_bumper; // Actual state of bumper

            if (y == 0 || x == 0) rx *= 0.8; // Puts motor power at 80% when the robot is turning-in-place

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

            telemetry.addData("Speed: ", powerMultiplier); // Shown on DS for reference


            // Right bumper on gamepad 2 for open/close claw
            if (g2RightBumperInput && !g2RightBumperPrevious) { // Refer to GamePad 1's conditional above
                g2RightBumperPressed = !g2RightBumperPressed;
            }

            g2RightBumperPrevious = g2RightBumperInput; // Update previous

            if (g2RightBumperPressed) claw.setPosition(0.63); // Closed Claw Position
            if (!g2RightBumperPressed) claw.setPosition(1.0); // Open Claw Position

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
            telemetry.addData("Front Left Motor ", frontLeftPower * powerMultiplier * 100);
            telemetry.addData("Front Right Motor ", frontRightPower * powerMultiplier * 100);
            telemetry.addData("Back Left Motor ", backLeftPower * powerMultiplier * 100);
            telemetry.addData("Back Right Motor ", backRightPower * powerMultiplier * 100);

            telemetry.addData("servo pos: ", claw.getPosition());

            telemetry.update(); // Update all the data on the DS telemetry window
        }
    }
}