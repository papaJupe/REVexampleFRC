// REV closeLooPositControl+auto              Robot.j
// from REV closeLoop example, mod here to test PID position on one NEO motor.
// tuned param for unloaded Neo rotation 10-100 ; no manual control coded. 
// this:
// +auto version, add single autoComm using position, use SmtDash for var display
// testing isFin() of cmd, user control of tele position. All working ==> finalize; 
// next version will test sequential auto cmd, add manual control in tele

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import static frc.robot.DriveSubsys.*;

public class Robot extends TimedRobot {

  public static DriveSubsys _myDrive = new DriveSubsys();

  private Command _autoComm, _teleComm; // used in [autoInit | teleInit]
  // to call GTP cmd with fixed vs. user settable distance param

  private double _driveSetting; // setpoint input from user via SmtDash

  @Override
  public void robotInit() {

    // display PID coefficients (from subsys import) on SmartDashboard
    SmartDashboard.putNumber("P Gain", kP);
    SmartDashboard.putNumber("I Gain", kI);
    SmartDashboard.putNumber("D Gain", kD);
    SmartDashboard.putNumber("I Zone", kIz);
    SmartDashboard.putNumber("Feed Forward", kFF);
    SmartDashboard.putNumber("Max Output", kMaxOutput);
    SmartDashboard.putNumber("Min Output", kMinOutput);

    // to set desired position & show actual encoder value
    SmartDashboard.putNumber("driveSetting", 0);
    SmartDashboard.putNumber("encodValue", 0);
    // show status of GoToPosition cmd
    SmartDashboard.putString("GTPcmd fin?", "???");

      } // end roboInit

  @Override
  public void robotPeriodic() { // as good here as in teleoPerio
    // read current PID coeffic from SD --> new var setting prn
    double p = SmartDashboard.getNumber("P Gain", 0);
    double i = SmartDashboard.getNumber("I Gain", 0);
    double d = SmartDashboard.getNumber("D Gain", 0);
    double iz = SmartDashboard.getNumber("I Zone", 0);
    double ff = SmartDashboard.getNumber("Feed Forward", 0);
    double max = SmartDashboard.getNumber("Max Output", 0);
    double min = SmartDashboard.getNumber("Min Output", 0);

    // if PID coeffic on SmartDash changed, write new value to controller
    // ? writing to static var, so reset in subsys as well I hope
    if ((p != kP)) {
      _leftPIDControl.setP(p);
      kP = p;
    }
    if ((i != kI)) {
      _leftPIDControl.setI(i);
      kI = i;
    }
    if ((d != kD)) {
      _leftPIDControl.setD(d);
      kD = d;
    }
    if ((iz != kIz)) {
      _leftPIDControl.setIZone(iz);
      kIz = iz;
    }
    if ((ff != kFF)) {
      _leftPIDControl.setFF(ff);
      kFF = ff;
    }
    if ((max != kMaxOutput) || (min != kMinOutput)) {
      _leftPIDControl.setOutputRange(min, max);
      kMinOutput = min;
      kMaxOutput = max;
    }

    // SmartDashboard.putNumber("RotatSetting", rotations);
    SmartDashboard.putNumber("encodValue", _leftEncoder.getPosition());
    CommandScheduler.getInstance().run();

  } // end roboPeri

  @Override
  public void autonomousInit() {  
    // rezero encoder reading every auto startup
    _leftEncoder.setPosition(0);
    double dist = 42; // how far to auto in inches
    _autoComm = new GoToPosition(dist);
    _autoComm.schedule();
  } // end autoInit

  @Override
  public void autonomousPeriodic() {
    // // scheduled autoComm runs fine without adding this
    // m_pidController.setReference(dist, CANSparkMax.ControlType.kPosition);

  }

  @Override
  public void teleopInit() {
    // rezero encoder reading every teleop startup
    _leftEncoder.setPosition(0);  
    // get updated position setting if there is one on SmtDash
    _driveSetting = SmartDashboard.getNumber("driveSetting", 0);
    _teleComm = new GoToPosition( _driveSetting);
    _teleComm.schedule();  // needs to be here for one-off cmd to work
  } // end teleopInit

  @Override
  public void teleopPeriodic() {  
    SmartDashboard.putNumber("encodValue", _leftEncoder.getPosition());
    // this worked, used rot/drive var set by SD field, motor class's method;
    // but bypasses GTP cmd, so no isFin evaluated
    // _leftPIDControl.setReference(_driveSetting,
    //                CANSparkMax.ControlType.kPosition);
    // I use GTP instead in teleInit, to assure finish occured
   
  } // end teleoPeriod

@Override
public void disabledInit() {
// needed to reset sticky var in code, problematic on repeat cmd call
// mainly a problem SD displaying old values
  _leftEncoder.setPosition(0);

  // refresh PID coefficient (from current code var) on SmartDashboard
  SmartDashboard.putNumber("P Gain", kP);
  SmartDashboard.putNumber("I Gain", kI);
  SmartDashboard.putNumber("D Gain", kD);
  SmartDashboard.putNumber("I Zone", kIz);
  SmartDashboard.putNumber("Feed Forward", kFF);
  SmartDashboard.putNumber("Max Output", kMaxOutput);
  SmartDashboard.putNumber("Min Output", kMinOutput);
  // set desired position & show actual encod value (should be 0)
  SmartDashboard.putNumber("driveSetting", 0);
  SmartDashboard.putNumber("encodValue",  _leftEncoder.getPosition());
  SmartDashboard.putString("GTPcmd fin?", "???");

}  // end disabledInit


} // end robo.j
