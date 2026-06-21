package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import BLL.Empresa;
import BLL.Persona;
import BLL.Rol;
import DLL.AutenticacionController;
import DLL.RegistroController;

public class MainMenuGrafico extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel pnlDinamico;
    
    // Ruta del GIF original que tenías en tu Main
    private final String RUTA_GIF = "";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainMenuGrafico frame = new MainMenuGrafico();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainMenuGrafico() {
        setTitle("HOUSEHUNTER v1.0 - PORTAL CORPORATIVO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 500);
        setLocationRelativeTo(null); // Centrado en pantalla
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // --- SECCIÓN IZQUIERDA: IMAGEN / GIF INSTITUCIONAL ---
        JPanel pnlIzquierdo = new JPanel(new BorderLayout());
        pnlIzquierdo.setBackground(Color.BLACK);
        pnlIzquierdo.setBorder(new LineBorder(Color.DARK_GRAY));
        contentPane.add(pnlIzquierdo, BorderLayout.WEST);

        JLabel lblBanner = new JLabel("");
        try {
            lblBanner.setIcon(new ImageIcon(RUTA_GIF));
        } catch (Exception e) {
            lblBanner.setText("HOUSE HUNTER");
            lblBanner.setForeground(Color.WHITE);
            lblBanner.setHorizontalAlignment(SwingConstants.CENTER);
        }
        pnlIzquierdo.add(lblBanner, BorderLayout.CENTER);

        // --- SECCIÓN DERECHA: PANEL DINÁMICO INTERACTIVO ---
        pnlDinamico = new JPanel();
        pnlDinamico.setBackground(new Color(248, 249, 250));
        pnlDinamico.setBorder(new EmptyBorder(30, 40, 30, 40));
        pnlDinamico.setLayout(new BorderLayout(0, 20));
        contentPane.add(pnlDinamico, BorderLayout.CENTER);

        // Mostrar la vista de bienvenida inicial por defecto
        mostrarVistaInicial();
    }

    private void cambiarPanelDinamico(JPanel nuevoPanel) {
        pnlDinamico.removeAll();
        pnlDinamico.add(nuevoPanel, BorderLayout.CENTER);
        pnlDinamico.revalidate();
        pnlDinamico.repaint();
    }

    /**
     * VISTA 1: Pantalla de Bienvenida con opciones de Iniciar o Registrarse
     */
    private void mostrarVistaInicial() {
        JPanel pnlInicio = new JPanel(new GridBagLayout());
        pnlInicio.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("Bienvenido a HouseHunter");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(33, 37, 41));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        pnlInicio.add(lblTitulo, gbc);

        JLabel lblSubtitulo = new JLabel("Gestor de accesos hoteleros corporativos.");
        lblSubtitulo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblSubtitulo.setForeground(Color.GRAY);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        pnlInicio.add(lblSubtitulo, gbc);

        // Espaciador gráfico
        gbc.gridy = 2;
        pnlInicio.add(new JLabel(" "), gbc);

        JButton btnLogin = new JButton("INICIAR SESIÓN");
        btnLogin.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> mostrarVistaLogin());
        gbc.gridy = 3;
        pnlInicio.add(btnLogin, gbc);

        JButton btnRegistro = new JButton("REGISTRAR NUEVA CUENTA");
        btnRegistro.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnRegistro.setBackground(new Color(40, 167, 69));
        btnRegistro.setForeground(Color.WHITE);
        btnRegistro.setFocusPainted(false);
        btnRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegistro.addActionListener(e -> mostrarVistaPreRegistro());
        gbc.gridy = 4;
        pnlInicio.add(btnRegistro, gbc);

        cambiarPanelDinamico(pnlInicio);
    }

    /**
     * VISTA 2: Formulario Integrado de Login (Adiós a los OptionDialogs colgados)
     */
    private void mostrarVistaLogin() {
        JPanel pnlLogin = new JPanel(new GridBagLayout());
        pnlLogin.setOpaque(false);
        pnlLogin.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY), "Ingreso Seguro al Sistema", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 12), new Color(0, 102, 204)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Email
        gbc.gridx = 0; gbc.gridy = 0;
        pnlLogin.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField txtEmail = new JTextField();
        txtEmail.setFont(new Font("Tahoma", Font.PLAIN, 12));
        pnlLogin.add(txtEmail, gbc);

        // Fila 1: Contraseña
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        pnlLogin.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JPasswordField txtPassword = new JPasswordField();
        pnlLogin.add(txtPassword, gbc);

        // Fila 2: Botonera integrada
        JPanel pnlBotones = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlBotones.setOpaque(false);

        JButton btnVolver = new JButton("VOLVER");
        btnVolver.addActionListener(e -> mostrarVistaInicial());
        pnlBotones.add(btnVolver);

        JButton btnIngresar = new JButton("INGRESAR");
        btnIngresar.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnIngresar.setBackground(new Color(0, 102, 204));
        btnIngresar.setForeground(Color.WHITE);
        
        // Acción de Ingreso con Enter o clic
        Runnable accionLoguear = () -> {
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Por favor complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Persona usuario = AutenticacionController.getInstance().iniciarSesion(email, password);

            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "¡Login exitoso!\nBienvenido " + usuario.getNombre(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cierra el menú principal de login de raíz sin dejar residuos
                
                if (usuario.getRol() == Rol.EMPRESA && usuario instanceof Empresa) {
                    Empresa empresaLogueada = (Empresa) usuario;
                    new EmpresaMenuGrafico(empresaLogueada).setVisible(true);
                } else {
                    usuario.mostrarMenu(); // Lanza AdministradorMenuGrafico u otros dinámicamente
                }
            } else {
                JOptionPane.showMessageDialog(this, "❌ Credenciales inválidas. Intente nuevamente.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            }
        };

        btnIngresar.addActionListener(e -> accionLoguear.run());
        txtPassword.addActionListener(e -> accionLoguear.run()); // Soporte nativo para apretar Enter

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        pnlBotones.add(btnIngresar);
        pnlLogin.add(pnlBotones, gbc);

        cambiarPanelDinamico(pnlLogin);
    }

    /**
     * VISTA 3: Selector de Rol para Registro
     */
    private void mostrarVistaPreRegistro() {
        JPanel pnlPreReg = new JPanel(new GridBagLayout());
        pnlPreReg.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblDuda = new JLabel("¿Qué tipo de cuenta corporativa desea crear?");
        lblDuda.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblDuda.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        pnlPreReg.add(lblDuda, gbc);

        JButton btnEmpresa = new JButton("CUENTA DE EMPRESA ORGANIZADORA");
        btnEmpresa.addActionListener(e -> mostrarVistaRegistroForm(Rol.EMPRESA));
        gbc.gridy = 1;
        pnlPreReg.add(btnEmpresa, gbc);

        JButton btnInvitado = new JButton("CUENTA DE INVITADO / HUÉSPED");
        btnInvitado.addActionListener(e -> mostrarVistaRegistroForm(Rol.INVITADO));
        gbc.gridy = 2;
        pnlPreReg.add(btnInvitado, gbc);

        JButton btnAtras = new JButton("VOLVER AL MENÚ");
        btnAtras.addActionListener(e -> mostrarVistaInicial());
        gbc.gridy = 3;
        pnlPreReg.add(btnAtras, gbc);

        cambiarPanelDinamico(pnlPreReg);
    }

    /**
     * VISTA 4: Formulario Dinámico de Registro según Rol seleccionado
     */
    private void mostrarVistaRegistroForm(Rol rolElegido) {
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setOpaque(false);
        pnlForm.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY), "Registro de " + rolElegido.toString(), TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 12), new Color(40, 167, 69)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos Base Comunes
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        pnlForm.add(new JLabel("Email corporativo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField txtEmail = new JTextField();
        pnlForm.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        pnlForm.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JPasswordField txtPass = new JPasswordField();
        pnlForm.add(txtPass, gbc);

        // Campos variables según el rol elegido
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        JLabel lblVar1 = new JLabel(rolElegido == Rol.EMPRESA ? "CUIT:" : "Nombre:");
        pnlForm.add(lblVar1, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField txtVar1 = new JTextField();
        pnlForm.add(txtVar1, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        JLabel lblVar2 = new JLabel(rolElegido == Rol.EMPRESA ? "Razón Social:" : "Apellido:");
        pnlForm.add(lblVar2, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField txtVar2 = new JTextField();
        pnlForm.add(txtVar2, gbc);

        // El invitado lleva DNI, la empresa no usa tercer campo
        JTextField txtVar3 = new JTextField();
        if (rolElegido == Rol.INVITADO) {
            gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0;
            pnlForm.add(new JLabel("DNI:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            pnlForm.add(txtVar3, gbc);
        }

        // Fila Final: Botonera de Envío
        JPanel pnlBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlBotones.setOpaque(false);
        
        JButton btnCancelar = new JButton("CANCELAR");
        btnCancelar.addActionListener(e -> mostrarVistaPreRegistro());
        pnlBotones.add(btnCancelar);

        JButton btnRegistrar = new JButton("CREAR CUENTA");
        btnRegistrar.setBackground(new Color(40, 167, 69));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Tahoma", Font.BOLD, 11));
        
        btnRegistrar.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            String d1 = txtVar1.getText().trim();
            String d2 = txtVar2.getText().trim();
            String d3 = (rolElegido == Rol.INVITADO) ? txtVar3.getText().trim() : null;

            if (email.isEmpty() || pass.isEmpty() || d1.isEmpty() || d2.isEmpty() || (rolElegido == Rol.INVITADO && d3.isEmpty())) {
                JOptionPane.showMessageDialog(this, "⚠️ Todos los campos son obligatorios.", "Error de validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean exito = RegistroController.getInstance().procesarRegistro(email, pass, rolElegido, d1, d2, d3);
            if (exito) {
                JOptionPane.showMessageDialog(this, "🎉 ¡Cuenta registrada de forma segura!\nYa puede iniciar sesión.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                mostrarVistaLogin(); // Lo manda directo a loguearse
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al guardar. El correo o DNI ya existe.", "Duplicación de Datos", JOptionPane.ERROR_MESSAGE);
            }
        });
        pnlBotones.add(btnRegistrar);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weightx = 1.0;
        pnlForm.add(pnlBotones, gbc);

        cambiarPanelDinamico(pnlForm);
    }
}