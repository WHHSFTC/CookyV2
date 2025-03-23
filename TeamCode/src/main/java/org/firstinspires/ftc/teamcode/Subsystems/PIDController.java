package org.firstinspires.ftc.teamcode.Subsystems;

/**
 * PIDController class for precise motor control.
 * Uses Proportional (P), Integral (I), and Derivative (D) control to correct motor power.
 */

// Comments where @param is used are denoting descriptions which appear when hovering over the variable
public class PIDController {
    // PID coefficients (constants that determine responsiveness)
    private double kP, kI, kD;

    // Integral sum: Accumulates past errors (for the "I" term)
    private double integralSum = 0;

    // Last error value: Used to compute the derivative term (D)
    private double lastError = 0;

    // Last recorded time (in nanoseconds) to calculate time change (Δt)
    private long lastTime = System.nanoTime();

    /**
     * Constructor: Initializes the PID controller with given tuning values.
     *
     * @param kP Proportional coefficient (how strongly the system reacts to error)
     * @param kI Integral coefficient (helps correct small, long-term errors)
     * @param kD Derivative coefficient (helps smooth out sudden changes)
     */
    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    /**
     * Calculates the motor power adjustment needed based on the target and current position.
     *
     * @param target  The desired position (setpoint)
     * @param current The actual position (sensor reading, like an encoder value)
     * @return The output correction value to adjust motor power
     */
    public double calculate(double target, double current) {
        // Error: Difference between where we want to be and where we are
        double error = target - current;

        // Get the current time and compute the time difference (Δt) in seconds
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastTime) / 1e9; // Convert nanoseconds to seconds

        // Proportional term: Directly based on error (larger error = stronger response)
        double P = kP * error;

        // Integral term: Accumulates past errors over time to correct small constant errors
        // Only accumulate if deltaTime > 0 to prevent divide-by-zero issues
        integralSum += error * deltaTime;
        double I = kI * integralSum;

        // Derivative term: Reacts to the rate of change of error (how fast it's changing)
        double derivative = (error - lastError) / deltaTime;
        double D = kD * derivative;

        // Store the current error and time for the next loop
        lastError = error;
        lastTime = currentTime;

        // Return the total correction (P + I + D)
        return P + I + D;
    }

    /**
     * Resets the integral sum and last error.
     * Useful when stopping movement to prevent the integral from building up incorrectly.
     */
    public void reset() {
        integralSum = 0;
        lastError = 0;
    }

    /**
     * Allows tuning of the PID constants dynamically.
     *
     * @param kP New proportional coefficient
     * @param kI New integral coefficient
     * @param kD New derivative coefficient
     */
    public void setConstants(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }
}
