// closeLooPosCtlCmdAuto - REV                      Robot.j

// base: mod REV example to test/tune PID position on one NEO motor,
//      use SmtDash for var display & live param edit
// this vers. add single auto Cmd to position, ensure isFin() for cmd, user 
// can cmd to position in teleOp from SmtDash. All working in bench setup.
 
// next version add sequential auto cmd, manual stick control pos. in teleOp

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import static frc.robot.DriveSubsys.*;

public class Robot extends TimedRobot {

  public static DriveSubsys myDrive = new DriveSubsys();
  
  private Command autoComm, teleComm; // used in [autoInit | teleInit]
  // to call GTP cmd with fixed vs. user settable distance param

  private double driveSetting; // setpoint input from user via SmtDash

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

    // to set desired position
    SmartDashboard.putNumber("driveSetting", 0);
    // show actual encoder value
    SmartDashboard.putNumber("encodValue", 0);
    // show status of GoToPosition cmd
    SmartDashboard.putString("GTPcmd fin?", "???");

      } // end roboInit

  @Override
  public void robotPeriodic() { // works here as in teleoPerio before
    // get current PID coeffic from SD --> new user setting prn
    double p = SmartDashboard.getNumber("P Gain", 0);
    double i = SmartDashboard.getNumber("I Gain", 0);
    double d = SmartDashboard.getNumber("D Gain", 0);
    double iz = SmartDashboard.getNumber("I Zone", 0);
    double ff = SmartDashboard.getNumber("Feed Forward", 0);
    double max = SmartDashboard.getNumber("Max Output", 0);
    double min = SmartDashboard.getNumber("Min Output", 0);

    // if PID coeffic on SmartDash changed, write new value to controller
    //  writing to static var, so reset in subsys as well, I hope
    if ((p != kP)) {
      leftPIDControl.setP(p);
      kP = p;
    }
    if ((i != kI)) {
      leftPIDControl.setI(i);
      kI = i;
    }
    if ((d != kD)) {
      leftPIDControl.setD(d);
      kD = d;
    }
    if ((iz != kIz)) {
      leftPIDControl.setIZone(iz);
      kIz = iz;
    }
    if ((ff != kFF)) {
      leftPIDControl.setFF(ff);
      kFF = ff;
    }
    if ((max != kMaxOutput) || (min != kMinOutput)) {
      leftPIDControl.setOutputRange(min, max);
      kMinOutput = min;
      kMaxOutput = max;
    }

    // SmartDashboard.putNumber("RotatSetting", rotations);
    SmartDashboard.putNumber("encodValue", leftEncoder.getPosition());
    CommandScheduler.getInstance().run();

  } // end roboPeri

  @Override
  public void autonomousInit() {  
    // rezero encoder reading on every auto startup
    leftEncoder.setPosition(0);
    double dist = 42; // how far to auto in inches (rot. here)
    autoComm = new GoToPosition(dist);
    autoComm.schedule();
  } // end autoInit

  @Override
  public void autonomousPeriodic() {
    // // scheduled autoComm runs fine without adding this
    // m_pidController.setReference(dist, CANSparkMax.ControlType.kPosition);
  }

  @Override
  public void teleopInit() {
    // rezero encoder reading every teleop startup
    leftEncoder.setPosition(0);  
    // get updated position setting if there is one on SmtDash
    driveSetting = SmartDashboard.getNumber("driveSetting", 0);
    teleComm = new GoToPosition( driveSetting);
    teleComm.schedule();  // needs to be here for one-off cmd to work
  } // end teleopInit

  @Override
  public void teleopPeriodic() {  
    SmartDashboard.putNumber("encodValue", leftEncoder.getPosition());
    // this worked, used rot/drive var set by SmtDash field, motor class's
    //  method; but bypasses GTP cmd, so no isFin() evaluated
    // leftPIDControl.setReference(_driveSetting,
    //                CANSparkMax.ControlType.kPosition);
    // I use GTP instead in teleInit, to assure cmd finishes
  } // end teleoPeriod

@Override
public void disabledInit() {
// needed to reset sticky var in code, problematic on repeat cmd call,
// mainly a problem of SmtDash displaying old values
  leftEncoder.setPosition(0);

  // refresh PID coefficient (from current code var) on SmartDashboard
  SmartDashboard.putNumber("P Gain", kP);
  SmartDashboard.putNumber("I Gain", kI);
  SmartDashboard.putNumber("D Gain", kD);
  SmartDashboard.putNumber("I Zone", kIz);
  SmartDashboard.putNumber("Feed Forward", kFF);
  SmartDashboard.putNumber("Max Output", kMaxOutput);
  SmartDashboard.putNumber("Min Output", kMinOutput);
  // can set desired position 
  SmartDashboard.putNumber("driveSetting", 0);
  //show actual encod value (should be 0 from above())
  SmartDashboard.putNumber("encodValue",  leftEncoder.getPosition());
  SmartDashboard.putString("GTPcmd fin?", "???");

}  // end disabledInit

} // end robo.j
