// closeLooPosCtlCmdAuto                         DriveSubsys.j

// need to tune PID param with REV client when/if real mechanism

package frc.robot;

import com.revrobotics.CANSparkMax;
// import com.revrobotics.CANSparkMax.ControlType.*;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.*;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DriveSubsys extends SubsystemBase {

  // CAN bus ID  = 13;
  public CANSparkMax leftMaster = new CANSparkMax(13, MotorType.kBrushless);

  public static SparkMaxPIDController leftPIDControl;
   // value used in roboInit/Period, GTP fin
  public static RelativeEncoder leftEncoder; 

  public static double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;
  // drive goal in inch, var sent to  SmtDash too
  public static double inchTarget = 0; 

  // CONSTRUCT a new __Subsystem. 
  public DriveSubsys() {
    leftMaster.restoreFactoryDefaults();
    leftMaster.setInverted(false);
    leftMaster.setIdleMode(IdleMode.kBrake);
    leftMaster.setOpenLoopRampRate(0.5);

    /* In order to use PID functions, a SparkMaxPIDController object
     * is constructed by calling the getPIDController() method of an existing
     * CANSparkMax object
     */
    leftPIDControl = leftMaster.getPIDController();

    // relativ Encoder object created to get position values
    // numbers here for chewbacca gear&wheel; now getEncoder returns inches
    leftEncoder = leftMaster.getEncoder(); // inch distance = to 1 motor rotat
    leftEncoder.setPositionConversionFactor(Math.PI * 6 / 8); // ~2.2

    // PID coeffic tuned for NEO bare motor in pos mode, no load
    // can be reset by user from SmtDash (v. robotPeriodic)
    kP = 0.15;
    kI = 0;
    kD = 0;
    kIz = 0;
    kFF = 0;
    kMaxOutput = 0.1;
    kMinOutput = -0.1;

    // set PID coefficients
    leftPIDControl.setP(kP);
    leftPIDControl.setI(kI);
    leftPIDControl.setD(kD);
    leftPIDControl.setIZone(kIz);
    leftPIDControl.setFF(kFF);
    leftPIDControl.setOutputRange(kMinOutput, kMaxOutput);
     // values should hold thru power cycle
    leftMaster.burnFlash();

  } // end constructor

  /* PIDController objects are commanded to a set point using the
   * setReference() method.
   * 
   * The first parameter is the value of the set point, whose units vary
   * depending on the control type set in the second parameter.
   * 
   * The second parameter is the control type -- can be one of 4 parameters:
   * com.revrobotics.CANSparkMax.ControlType.kDutyCycle
   * com.revrobotics.CANSparkMax.ControlType.kPosition
   * com.revrobotics.CANSparkMax.ControlType.kVelocity
   * com.revrobotics.CANSparkMax.ControlType.kVoltage
   * example m_pidController.setReference
   *                    (rotations, CANSparkMax.ControlType.kPosition);
   */

  // param received here in inches;
  // init() of GoToPosition cmd re-zeros encoder, so don't repeat here
  // in R/L motor drivetrain, using PID pos control could help straight path?
  public void goStraightPosition(double target) {
    leftPIDControl.setReference(target, CANSparkMax.ControlType.kPosition);
  }

  // method called from GTP's init()
  public void zeroEncoder() { 
    leftEncoder.setPosition(0.0);

  }  // end goStraightPos

  // @return value of some boolean subsystem state, such as a digital sensor.
   
  // public boolean exampleCondition() {
  // // Query some boolean state, such as a digital sensor.
  // return false;
  // }

  @Override
  public void periodic() {
    // code here will be called once per scheduler run
  }

} // end subsys class
