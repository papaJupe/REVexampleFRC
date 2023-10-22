// closeLooPosCtl+SeqAuto                               DriveSubsys
 
// must tune PID param with REV client when/if real drive mechanism

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.*;
import com.revrobotics.CANSparkMax.IdleMode;


import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import edu.wpi.first.wpilibj.SpeedController;

public class DriveSubsys extends SubsystemBase {

  // CAN bus
  // leftMasterID = 13;

  public CANSparkMax _leftMaster = new CANSparkMax(13, MotorType.kBrushless);
  public static SparkMaxPIDController _leftPIDControl;
   // used in roboInit/Period, GTP fin?
  public static RelativeEncoder _leftEncoder; 
  
  public static double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;
  public static double inchTarget = 0; // drive goal in inch, var sent to SD

  /* CONSTRUCT a new Subsystem instance */
  public DriveSubsys() {
     _leftMaster.restoreFactoryDefaults();

    _leftMaster.setInverted(false);
    _leftMaster.setIdleMode(IdleMode.kBrake);
    _leftMaster.setOpenLoopRampRate(0.5);

    /* In order to use PID functionality, a SparkMaxPIDController object
     * is constructed with the getPIDController() method of an existing
     * CANSparkMax object
     */
    _leftPIDControl = _leftMaster.getPIDController();

    // relative Encoder object created to get position values
    // for chewbacca gear&wheel:  gear ratio 8:1, wheel diam 6"
    _leftEncoder = _leftMaster.getEncoder(); // inch distance = to 1 rota
    _leftEncoder.setPositionConversionFactor(Math.PI * 6 / 8); // ~2.2

    // PID coeffic tuned for NEO bare motor position mode, no load
    kP = 0.15;  // could set these val above in class var declaration
    kI = 0;
    kD = 0;
    kIz = 0;
    kFF = 0;
    kMaxOutput = 0.1;
    kMinOutput = -0.1;

      // set PID coefficients for the controller
    _leftPIDControl.setP(kP);
    _leftPIDControl.setI(kI);
    _leftPIDControl.setD(kD);
    _leftPIDControl.setIZone(kIz);
    _leftPIDControl.setFF(kFF);
    _leftPIDControl.setOutputRange(kMinOutput, kMaxOutput);

    _leftMaster.burnFlash(); // save to firmware

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
  public void goStraightPosition(int target) {

    _leftPIDControl.setReference(target, CANSparkMax.ControlType.kPosition);
  }

  // method called from zeroDrivEncod cmd & GTP's endme(), need param = 0
  public void zeroEncoder(int pos) { // [pos,indx,timeout]
    _leftEncoder.setPosition(pos);
  }
  
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    // enables joystick control of motor rotation when
    // teleOp enabled; acts like default cmd for the subsys
    if (Math.abs(Robot._myJoy.getRawAxis(1)) > 0.04) 
    _leftMaster.set(Robot._myJoy.getRawAxis(1) * -0.2);
    else _leftMaster.set(0);
  }
  // @return value of some boolean subsystem state, such as a digital sensor
  // public boolean exampleCondition() {
  // // Query some boolean state, such as a digital sensor.
  // return false;
  // }
} // end subsys class
