package ReproductorMusical;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import javazoom.jl.player.advanced.AdvancedPlayer;


/**
 *
 *
 * @author marye
 */
public class Reproductor {

    private JPanel panelReproductor;
    private Cancion cancionActual;

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private int lastFrame = 0;
    private Runnable onFinished;

    private JLabel lblImgDefault;
    private JLabel lblTitulo;
    private JButton btnPlayPause;
    private JButton btnStop;
    private JProgressBar barraProgreso;
    private JLabel lblTiempo;

    private Timer timer;
    private long tiempoTranscurrido = 0;
    private long duracionTotal = 0;

    private AdvancedPlayer player;
    private Thread reproductorThread;
    private FileInputStream fis;
    private BufferedInputStream bis;
 
    public Reproductor() {
        initComponents();
        iniciarTimerReproduccion();
    }

     public JPanel getPanel() {
        return panelReproductor;
    }

    public Cancion getCancionActual() {
        return cancionActual;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setOnFinished(Runnable r) {
        this.onFinished = r;
    }

    private void initComponents() {

        panelReproductor = new JPanel();
        panelReproductor.setLayout(new BorderLayout());
        panelReproductor.setBackground(new Color(40, 40, 40));
        panelReproductor.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(80, 80, 80)));
        panelReproductor.setPreferredSize(new Dimension(0, 110));

        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(new Color(40, 40, 40));
        panelInfo.setPreferredSize(new Dimension(250, 0));

        JPanel panelTexto = new JPanel(new GridLayout(1, 1));
        panelTexto.setBackground(new Color(40, 40, 40));
        panelTexto.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        lblTitulo = new JLabel("Selecciona una canción");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        panelTexto.add(lblTitulo);
        panelInfo.add(panelTexto, BorderLayout.CENTER);

        lblImgDefault = new JLabel();
        lblImgDefault.setPreferredSize(new Dimension(100, 100));
        lblImgDefault.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        lblImgDefault.setHorizontalAlignment(SwingConstants.CENTER);
        lblImgDefault.setVerticalAlignment(SwingConstants.CENTER);

        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(40, 40, 40));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelProgreso = new JPanel(new BorderLayout());
        panelProgreso.setBackground(new Color(40, 40, 40));
        panelProgreso.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setBackground(new Color(80, 80, 80));
        barraProgreso.setForeground(new Color(29, 185, 84));
        barraProgreso.setBorderPainted(false);
        barraProgreso.setPreferredSize(new Dimension(420, 12));
        barraProgreso.setMaximumSize(new Dimension(420, 12));
        lblTiempo = new JLabel("0:00 / 0:00");
        lblTiempo.setForeground(new Color(179, 179, 179));
        lblTiempo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTiempo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTiempo.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        panelProgreso.add(barraProgreso, BorderLayout.CENTER);
        panelProgreso.add(lblTiempo, BorderLayout.SOUTH);

        JPanel panelControles = new JPanel(new GridLayout(1, 2, 10, 0));
        panelControles.setBackground(new Color(40, 40, 40));
        panelControles.setPreferredSize(new Dimension(420, 40));
        panelControles.setMaximumSize(new Dimension(420, 40));
        panelControles.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] nombres = {"REINICIAR", "DETENER"};
        JButton[] botones = new JButton[2];
        for (int i = 0; i < nombres.length; i++) {
            JButton b = new JButton(nombres[i]);
            b.setBackground(new Color(220, 53, 69));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setFont(new Font("Arial", Font.BOLD, 16));
            b.setPreferredSize(new Dimension(205, 36));
            b.setMinimumSize(new Dimension(205, 36));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setContentAreaFilled(false);
            b.setOpaque(true);
            b.setEnabled(false);
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (b.isEnabled()) {
                        b.setBackground(new Color(220, 53, 69).brighter());
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    b.setBackground(new Color(220, 53, 69));
                }
            });
            botones[i] = b;
            panelControles.add(b);
        }

        btnPlayPause = botones[0];
        btnStop = botones[1];

        panelDerecho.add(panelProgreso);
        panelDerecho.add(Box.createVerticalStrut(8));
        panelDerecho.add(panelControles);

        panelReproductor.add(lblImgDefault, BorderLayout.WEST);
        panelReproductor.add(panelInfo, BorderLayout.CENTER);
        panelReproductor.add(panelDerecho, BorderLayout.EAST);

        btnPlayPause.addActionListener(e -> playpause());
        btnStop.addActionListener(e -> stop());

    }
    
     private void iniciarTimerReproduccion() {

        timer = new Timer(1000, e -> {
            if (isPlaying && !isPaused) {
                tiempoTranscurrido++;
                actualizar();
                if (duracionTotal > 0 && tiempoTranscurrido >= duracionTotal) {
                    cancionFinalizada();
                }
            }
        });

    }

   public void cargarCancion(Cancion cancion) {

        if (cancion == null) {
            return;
        }

        stopPlayerFrame();
        this.cancionActual = cancion;
        contarFramesYMs(cancion.getDireccion());

        if (this.duracionTotal <= 0 && cancion.getDuracion() > 0) {
            this.duracionTotal = cancion.getDuracion();
        }

        tiempoTranscurrido = 0;
        lastFrame = 0;
        isPaused = false;

        lblTitulo.setText(cancion.getTitulo());
        lblImgDefault.setIcon(cancion.getImgDefault());
        btnPlayPause.setEnabled(true);
        btnStop.setEnabled(true);
        btnPlayPause.setText("REINICIAR");
        btnStop.setText("DETENER");
        actualizar();

    }

    public void reproducir(Cancion cancion) {

        if (cancion == null) {
            return;
        }

        cargarCancion(cancion);
        play();

    }

    public void play() {

        if (cancionActual == null) {
            return;
        }

        if (isPaused && lastFrame > 0) {
            isPaused = false;
            startPlayerFrame(lastFrame);
            return;
        }

        isPaused = false;
        lastFrame = 0;
        tiempoTranscurrido = 0;
        startPlayerFrame(0);
    }

    public void stop() {

        if (isPlaying) {
            int secondsFrames = Math.round(tiempoTranscurrido * 38);

            lastFrame = secondsFrames;
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            if (player != null) {
                try {
                    player.close();
                } catch (Exception ignored) {
                }
            }
            if (reproductorThread != null && reproductorThread.isAlive()) {
                reproductorThread.interrupt();
            }
            isPaused = true;
            isPlaying = false;
            btnStop.setText("CONTINUAR");
            btnPlayPause.setText("REINICIAR");
            closeStreamsSilently();
        } else if (isPaused) {
            isPaused = false;
            startPlayerFrame(lastFrame);
            btnStop.setText("DETENER");
        } else {
            play();
        }

    }


   public void playpause() {

        if (cancionActual == null) {
            return;
        }
        isPaused = false;
        isPlaying = false;
        lastFrame = 0;
        tiempoTranscurrido = 0;
        startPlayerFrame(0);
        btnStop.setText("DETENER");
        btnPlayPause.setText("REINICIAR");
    }

    private void startPlayerFrame(int startFrame) {

        stopPlayerFrame();
        File archivo = new File(cancionActual.getDireccion());

        if (!archivo.exists()) {
            return;
        }

        try {
            fis = new FileInputStream(archivo);
            bis = new BufferedInputStream(fis);
            player = new AdvancedPlayer(bis);

            reproductorThread = new Thread(() -> {
                try {
                    if (timer != null && !timer.isRunning()) {
                        timer.start();
                    }

                    if (startFrame > 0) {
                        player.play(startFrame, Integer.MAX_VALUE);
                    } else {
                        player.play();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            isPlaying = true;
            isPaused = false;
            reproductorThread.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cancionFinalizada() {

        if (cancionActual != null && cancionActual.getDuracion() <= 0) {
            cancionActual.setDuracion(tiempoTranscurrido);
        }

        stopPlayerFrame();
        isPlaying = false;
        isPaused = false;
        tiempoTranscurrido = 0;
        lastFrame = 0;

        if (timer != null) {
            timer.stop();
        }

        btnPlayPause.setText("REINICIAR");
        btnStop.setText("DETENER");
        actualizar();

        if (onFinished != null) {
            try {
                onFinished.run();
            } catch (Exception ignored) {
            }
        }

    }

    private void stopPlayerFrame() {

        if (player != null) {
            try {
                player.close();
            } catch (Exception ignored) {
            }
            player = null;
        }

        if (reproductorThread != null && reproductorThread.isAlive()) {
            reproductorThread.interrupt();
            reproductorThread = null;
        }

        closeStreamsSilently();

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

    }

    private void actualizar() {

        if (duracionTotal > 0) {
            int progreso = (int) ((double) tiempoTranscurrido / duracionTotal * 100);
            barraProgreso.setValue(Math.min(progreso, 100));
        } else {
            barraProgreso.setValue(0);
        }

        String tiempoActual = DuracionFormateada((int) tiempoTranscurrido);
        String tiempoTotal = DuracionFormateada((int) duracionTotal);
        lblTiempo.setText(tiempoActual + " / " + tiempoTotal);
    }

    private String DuracionFormateada(int segundos) {
        int minutos = segundos / 60;
        int segs = segundos % 60;
        return String.format("%d:%02d", minutos, segs);
    }

    private void closeStreamsSilently() {
        try {
            if (bis != null) {
                bis.close();
                bis = null;
            }
        } catch (IOException ignored) {
        }
        try {
            if (fis != null) {
                fis.close();
                fis = null;
            }
        } catch (IOException ignored) {
        }
    }

    private void contarFramesYMs(String direccion) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(direccion));
            AudioHeader header = audioFile.getAudioHeader();

            int duracionSegundos = header.getTrackLength();
            duracionTotal = duracionSegundos;

            if (cancionActual != null) {
                cancionActual.setDuracion(duracionSegundos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void limpiar() {
        stopPlayerFrame();
        cancionActual = null;
        lblTitulo.setText("Selecciona una canción");
        lblTiempo.setText("0:00 / 0:00");
        barraProgreso.setValue(0);
        btnPlayPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnPlayPause.setText("REINICIAR");
    }
}
