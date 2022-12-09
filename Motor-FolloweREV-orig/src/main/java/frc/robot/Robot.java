
package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

/**
 * This sample program shows how to control a motor using a joystick. In the
 * operator control part of the program, the joystick is read and the value is
 * written to the motor.
 *
 * Joystick analog values range from -1 to 1 and speed controller inputs also
 * range from -1 to 1 making it easy to work together.
 */
public class Robot extends TimedRobot {
    // private static final int leadDeviceID = 1;
    // private static final int followDeviceID = 2;

    // private static final int kJoystickPort = 0;

    private CANSparkMax m_leftMotorMaster;
    private CANSparkMax m_leftMotorFollow;
    private CANSparkMax m_rightMotorMaster;
    private CANSparkMax m_rightMotorFollow;

    private Joystick m_joystick;

    @Override
    public void robotInit() {
        /**
         * SPARK MAX controllers are intialized over CAN by instancing a CANSparkMax
         * object. The CAN ID, which can be configured using the SPARK MAX Client,
         * is passed as the first parameter. The motor type is passed as the second
         * parameter. Motor type can either be:
         * com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
         * com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushed
         * 
         * code below initializes brushless motors with CAN IDs. Change
         * these parameters to match your setup
         */
        m_leftMotorMaster = new CANSparkMax(1, MotorType.kBrushless);
        m_leftMotorFollow = new CANSparkMax(2, MotorType.kBrushless);
        m_rightMotorMaster = new CANSparkMax(3, MotorType.kBrushless);
        m_rightMotorFollow = new CANSparkMax(4, MotorType.kBrushless);

        /**
         * The RestoreFactoryDefaults method can be used to reset the configuration
         * parameters in the SPARK MAX to their factory default state. If no
         * argument is passed, these parameters will not persist between power cycles
         */
        m_leftMotorMaster.restoreFactoryDefaults();
        m_leftMotorFollow.restoreFactoryDefaults();
        m_rightMotorMaster.restoreFactoryDefaults();
        m_rightMotorFollow.restoreFactoryDefaults();

        /**
         * In CAN mode, one SPARK MAX can be configured to follow another.
         * This is done by calling the follow() method on the SPARK MAX
         * you want to configure as a follower, and by passing
         * as a parameter the SPARK MAX you want to configure as a leader.
         */
        m_leftMotorFollow.follow(m_leftMotorMaster);
        m_rightMotorFollow.follow(m_rightMotorMaster);

        // no effect if set to follow
        // m_leftMotorFollow.setInverted(false);
        // m_rightMotorFollow.setInverted(true);

        m_joystick = new Joystick(0);
    } // end robotInit

    @Override
    public void teleopPeriodic() {
        /**
         * m_followMotor will automatically follow whatever the applied output is on
         * m_leadMotor. Thus, set only needs to be called on leadMotor to control both
         */
        m_leftMotorMaster.set(m_joystick.getY());
    }
}
