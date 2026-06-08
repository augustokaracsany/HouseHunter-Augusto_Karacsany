package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.SystemColor;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
// Trabajar con JFrame y WindowBuilder Current.
public class AdministradorMenuGrafico extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdministradorMenuGrafico frame = new AdministradorMenuGrafico();
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
	public AdministradorMenuGrafico() {
		setTitle("HouseHunter - Menú de Administrador");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 950, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(625, 10));
		contentPane.add(panel, BorderLayout.WEST);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(AdministradorMenuGrafico.class.getResource("/img/HouseHunter_Menu-Administrador.png")));
		panel.add(lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(300, 10));
		contentPane.add(panel_1, BorderLayout.EAST);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(SystemColor.controlDkShadow);
		panel_2.setPreferredSize(new Dimension(10, 100));
		contentPane.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(null);
		
		JButton btnNewButton_1 = new JButton("Recepción.");
		btnNewButton_1.setBounds(10, 39, 130, 50);
		panel_2.add(btnNewButton_1);
		
		JButton btnNewButton_1_1 = new JButton("Actividades.\r\n");
		btnNewButton_1_1.setBounds(150, 39, 130, 50);
		panel_2.add(btnNewButton_1_1);
		
		JButton btnNewButton_1_1_1 = new JButton(" Reportes.");
		btnNewButton_1_1_1.setBounds(290, 39, 130, 50);
		panel_2.add(btnNewButton_1_1_1);
		
		JButton btnNewButton_1_1_1_1 = new JButton("Cerrar Sesión.\r\n");
		btnNewButton_1_1_1_1.setBounds(430, 39, 130, 50);
		panel_2.add(btnNewButton_1_1_1_1);

	}
}
