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
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.CompoundBorder;
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
		setBounds(100, 100, 1000, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.menu);
		panel_1.setPreferredSize(new Dimension(400, 10));
		contentPane.add(panel_1, BorderLayout.WEST);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(0, 0, 400, 300);
		panel_1.add(lblNewLabel);
		lblNewLabel.setIcon(new ImageIcon(EmpresaMenuGrafico.class.getResource("/img/HouseHunter_Menu-Empresa.png")));
		
		JPanel panel = new JPanel();
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setBackground(SystemColor.controlShadow);
		panel.setPreferredSize(new Dimension(10, 100));
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(null);
		
		JButton btnPlanificacin_1 = new JButton("RESERVAS.");
		btnPlanificacin_1.setBounds(946, 44, 112, 45);
		panel.add(btnPlanificacin_1);
		btnPlanificacin_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		buttonGroup.add(btnPlanificacin_1);
		btnPlanificacin_1.setFont(new Font("Arial", Font.BOLD, 9));
		
		JButton btnPlanificacin_1_1_3 = new JButton("CERRAR SESIÓN.");
		btnPlanificacin_1_1_3.setBounds(1068, 44, 112, 45);
		panel.add(btnPlanificacin_1_1_3);
		btnPlanificacin_1_1_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPlanificacin_1_1_3.setFont(new Font("Arial", Font.BOLD, 9));
		
		JButton btnPlanificacin_1_1 = new JButton("REPORTES.");
		btnPlanificacin_1_1.setBounds(824, 44, 112, 45);
		panel.add(btnPlanificacin_1_1);
		btnPlanificacin_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPlanificacin_1_1.setFont(new Font("Arial", Font.BOLD, 9));
		
		JButton btnPlanificacin_1_1_2_1 = new JButton("GESTIÓN.");
		btnPlanificacin_1_1_2_1.setBounds(10, 44, 112, 45);
		panel.add(btnPlanificacin_1_1_2_1);
		btnPlanificacin_1_1_2_1.setFont(new Font("Arial", Font.BOLD, 9));
		
		JButton btnPlanificacin_1_1_2 = new JButton("PLANIFICACIÓN.");
		btnPlanificacin_1_1_2.setBounds(132, 44, 112, 45);
		panel.add(btnPlanificacin_1_1_2);
		btnPlanificacin_1_1_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPlanificacin_1_1_2.setFont(new Font("Arial", Font.BOLD, 9));
		
		JButton btnPlanificacin_1_1_1 = new JButton("INVITACIONES.");
		btnPlanificacin_1_1_1.setBounds(254, 44, 112, 45);
		panel.add(btnPlanificacin_1_1_1);
		btnPlanificacin_1_1_1.setFont(new Font("Arial", Font.BOLD, 9));
		
		JLabel lblNewLabel_2_2 = new JLabel("Seleccionar Módulo de Gestión:");
		lblNewLabel_2_2.setBounds(535, 58, 149, 14);
		panel.add(lblNewLabel_2_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(SystemColor.menu);
		panel_2.setPreferredSize(new Dimension(380, 10));
		contentPane.add(panel_2, BorderLayout.EAST);
		panel_2.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("Reserva Activa: ID.");
		lblNewLabel_2.setBounds(139, 117, 102, 14);
		panel_2.add(lblNewLabel_2);
		
		JLabel lblNewLabel_1 = new JLabel("Panel de Empresa.");
		lblNewLabel_1.setBounds(99, 60, 182, 27);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panel_2.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2_1 = new JLabel("Entidad: Globant S.A.");
		lblNewLabel_2_1.setBounds(121, 98, 137, 14);
		panel_2.add(lblNewLabel_2_1);
		
		JLabel lblNewLabel_2_1_1 = new JLabel("Código de Evento: GLOBANT-2026");
		lblNewLabel_2_1_1.setBounds(99, 135, 182, 14);
		panel_2.add(lblNewLabel_2_1_1);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(SystemColor.controlDkShadow);
		contentPane.add(panel_3, BorderLayout.CENTER);

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
