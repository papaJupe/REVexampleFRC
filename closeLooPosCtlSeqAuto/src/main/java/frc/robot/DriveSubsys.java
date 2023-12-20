// closeLooPosCtlSeqAuto                            DriveSubsys.j
 
// must tune PID param with REV client when/if real drive mechanism

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.*;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DriveSubsys extends SubsystemBase {

  // CAN bus leftMasterID = 13;

  public CANSparkMax leftMaster = new CANSparkMax(13, MotorType.kBrushless);
  public static SparkMaxPIDController leftPIDControl;
   // used in roboInit/Period, GTP fin?
  public static RelativeEncoder leftEncoder; 
  
  public static double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;
  public static double inchTarget = 0; // drive goal in inch, var sent to SD

  /* CONSTRUCT a new Subsystem instance */
  public DriveSubsys() {
     leftMaster.restoreFactoryDefaults();

    leftMaster.setInverted(false);
    leftMaster.setIdleMode(IdleMode.kBrake);
    leftMaster.setOpenLoopRampRate(0.5);

    /* In order to use PID functionality, a SparkMaxPIDController object
     * is constructed with the getPIDController() method of an existing
     * CANSparkMax object
     */
    leftPIDControl = leftMaster.getPIDController();

    // relative Encoder object created to get position values
    // for chewbacca gear&wheel:  gear ratio 8:1, wheel diam 6"
    leftEncoder = leftMaster.getEncoder(); // inch distance = to 1 rota
    leftEncoder.setPositionConversionFactor(Math.PI * 6 / 8); // ~2.2

    // PID coeffic tuned for NEO bare motor position mode, no load
    kP = 0.15;  // could set these val above in class var declaration
    kI = 0;
    kD = 0;
    kIz = 0;
    kFF = 0;
    kMaxOutput = 0.1;
    kMinOutput = -0.1;

      // set PID coefficients for the controller
    leftPIDControl.setP(kP);
    leftPIDControl.setI(kI);
    leftPIDControl.setD(kD);
    leftPIDControl.setIZone(kIz);
    leftPIDControl.setFF(kFF);
    leftPIDControl.setOutputRange(kMinOutput, kMaxOutput);

    leftMaster.burnFlash(); // save to firmware

  } // end constructor

  /**
   * PIDController objects are commanded to a set point using the
   * setReference() method.
   * 
   * The first parameter is the value of the set point, whose units vary
   * depending on the control type set in the second parameter.
   * 
   * The second parameter is the control type -- one of these four:
   * com.revrobotics.CANSparkMax.ControlType.kDutyCycle
   * com.revrobotics.CANSparkMax.ControlType.kPosition
   * com.revrobotics.CANSparkMax.ControlType.kVelocity
   * com.revrobotics.CANSparkMax.ControlType.kVoltage  e.g.
   * m_pidController.setReference(rotat, CANSparkMax.ControlType.kPosition);
   */

  // param received here in inches; zeroEnco() called in 
  // init() of GoToPosition cmd, so don't repeat here
  public void goStraightPosition(double target) {

    leftPIDControl.setReference(target, CANSparkMax.ControlType.kPosition);
  }

  // method called from GTP's init()
  public void zeroEncoder() { // [pos,indx,timeout]
    leftEncoder.setPosition(0.0);
  }
  
  @Override
  public void periodic() {
    // This method is called once per scheduler run;
    // enables joystick control of motor rotation when
    // teleOp enabled; acts like default cmd for the subsys
    if (Math.abs(Robot.myJoy.getRawAxis(1)) > 0.04) 
             leftMaster.set(Robot.myJoy.getRawAxis(1) * -0.2);
    else leftMaster.set(0);
  }  // end periodic
  // @return value of some boolean subsystem state, such as a digital sensor
  // public boolean exampleCondition() {
  // // Query some boolean state, such as a digital sensor.
  // return false;
  // }
} // end subsys class
