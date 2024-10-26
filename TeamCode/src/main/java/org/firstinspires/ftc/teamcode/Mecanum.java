package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
//@Disabled
public class Mecanum extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        //Setting up the motors in the code based on the configuration names of the motors
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRight");//port 0
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeft"); //port 1
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRight");//port 2
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");//port 3

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        double powerMultiplier = 1.0;

        boolean rbPressed = false;
        boolean prevRBPressed = false;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.6;
            double rx = gamepad1.right_stick_x;

            boolean rightBumper = gamepad1.right_bumper;

            //Speed settings is toggled between slow and fast with one click of the right bumper

            if (rightBumper && !prevRBPressed) {
                rbPressed = !rbPressed;
            }
            prevRBPressed = rightBumper;
            if (rbPressed) powerMultiplier = 0.6;
            if (!rbPressed) powerMultiplier = 1.0;
            telemetry.addData("Speed: ", powerMultiplier);
            telemetry.update();

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double backLeftPower = (y - x + rx) / denominator;
            double frontLeftPower = (x + y + rx) / denominator;
            double backRightPower = (x + y - rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower * powerMultiplier);
            frontRightMotor.setPower(frontRightPower * powerMultiplier);
            backLeftMotor.setPower(backLeftPower * powerMultiplier);
            backRightMotor.setPower(backRightPower * powerMultiplier);
        }
    }
}