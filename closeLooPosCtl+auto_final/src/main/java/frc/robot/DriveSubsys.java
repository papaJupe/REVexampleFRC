// closeLooPositControl+autoMulti         DriveSubsys.j
// 
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// TO DO tune PID param with REV client when/if real drive object

package frc.robot;

//import static frc.robot.Constant.*;

import com.revrobotics.CANSparkMax;
// import com.revrobotics.CANSparkMax.ControlType.*;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.*;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DriveSubsys extends SubsystemBase {

  // CAN bus ID  = 13;
  public CANSparkMax _leftMaster = new CANSparkMax(13, MotorType.kBrushless);

  public static SparkMaxPIDController _leftPIDControl;

  public static RelativeEncoder _leftEncoder; // value used in roboInit/Period, GTP fin?

  public static double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;
  public static double inchTarget = 0; // drive goal in inch, var sent to SmtDash too

  /* CONSTRUCT a new __Subsystem. */
  public DriveSubsys() {
    _leftMaster.restoreFactoryDefaults();
    _leftMaster.setInverted(false);
    _leftMaster.setIdleMode(IdleMode.kBrake);
    _leftMaster.setOpenLoopRampRate(0.5);

    /* In order to use PID functionality, a SparkMaxPIDController object
     * is constructed by calling the getPIDController() method of an existing
     * CANSparkMax object
     */
    _leftPIDControl = _leftMaster.getPIDController();

    // relativ Encoder object created to get position values
    // numbers here for chewbacca gear&wheel; now getEncoder returns inches
    _leftEncoder = _leftMaster.getEncoder(); // inch distance = to 1 motor rotat
    _leftEncoder.setPositionConversionFactor(Math.PI * 6 / 8); // ~2.2

    // PID coeffic tuned for NEO bare motor in pos mode, no load
    kP = 0.15;
    kI = 0;
    kD = 0;
    kIz = 0;
    kFF = 0;
    kMaxOutput = 0.1;
    kMinOutput = -0.1;

    // set PID coefficients
    _leftPIDControl.setP(kP);
    _leftPIDControl.setI(kI);
    _leftPIDControl.setD(kD);
    _leftPIDControl.setIZone(kIz);
    _leftPIDControl.setFF(kFF);
    _leftPIDControl.setOutputRange(kMinOutput, kMaxOutput);

    _leftMaster.burnFlash();

  } // end constructor

  /* PIDController objects are commanded to a set point using the
   * setReference() method.
   * 
   * The first parameter is the value of the set point, whose units vary
   * depending on the control type set in the second parameter.
   * 
   * The second parameter is the control type -- can be one of four
   * parameters:
   * com.revrobotics.CANSparkMax.ControlType.kDutyCycle
   * com.revrobotics.CANSparkMax.ControlType.kPosition
   * com.revrobotics.CANSparkMax.ControlType.kVelocity
   * com.revrobotics.CANSparkMax.ControlType.kVoltage
   * m_pidController.setReference(rotations, CANSparkMax.ControlType.kPosition);
   */

  // param received here in inches; zeroEnco
  // called in periodic execute() of GoToPosition cmd, so don't zeroEnco here
  public void goStraightPosition(double target) {

    _leftPIDControl.setReference(target, CANSparkMax.ControlType.kPosition);
  }

  // method called from zeroDrivEncod cmd & GTP's endme(), need param == 0
  public void zeroEncoder(double pos) { // 
    _leftEncoder.setPosition(pos);

  }  // end goStraightPos

  /* @return value of some boolean subsystem state, such as a digital sensor.
   */
  // public boolean exampleCondition() {
  // // Query some boolean state, such as a digital sensor.
  // return false;
  // }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

} // end subsys class
