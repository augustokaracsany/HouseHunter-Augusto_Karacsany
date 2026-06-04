package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenu;
import javax.swing.JLabel;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.SystemColor;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.ButtonGroup;
import javax.swing.border.BevelBorder;
//Trabajar con JFrame y WindowBuilder Current. WIP
public class EmpresaMenuGrafico extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EmpresaMenuGrafico frame = new EmpresaMenuGrafico();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EmpresaMenuGrafico() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 341);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(400, 10));
		contentPane.add(panel_1, BorderLayout.WEST);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(EmpresaMenuGrafico.class.getResource("/img/HouseHunter_MenuGrafico-Empresa-v2.png")));
		panel_1.add(lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 100));
		contentPane.add(panel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("GESTIÓN DE EVENTO");
		buttonGroup.add(btnNewButton);
		btnNewButton.setFont(new Font("Arial", Font.BOLD, 9));
		btnNewButton.setBounds(0, 35, 135, 40);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		panel.setLayout(null);
		panel.add(btnNewButton);
		
		JButton btnPlanificacin = new JButton("PLANIFICACIÓN");
		buttonGroup.add(btnPlanificacin);
		btnPlanificacin.setFont(new Font("Arial", Font.BOLD, 9));
		btnPlanificacin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPlanificacin.setBounds(145, 35, 135, 40);
		panel.add(btnPlanificacin);
		
		JButton btnNewButton_1 = new JButton("INVITACIONES");
		buttonGroup.add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_1.setFont(new Font("Arial", Font.BOLD, 9));
		btnNewButton_1.setBounds(290, 35, 135, 40);
		panel.add(btnNewButton_1);
		
		JButton btnPlanificacin_1 = new JButton("REPORTES");
		buttonGroup.add(btnPlanificacin_1);
		btnPlanificacin_1.setFont(new Font("Arial", Font.BOLD, 9));
		btnPlanificacin_1.setBounds(435, 35, 135, 40);
		panel.add(btnPlanificacin_1);

	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
