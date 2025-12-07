package EditorTexto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author marye
 */
public class GestionTexto {
    

    public static DocumentModel cargarTxt(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException();
        }

        String texto = readAll(file);
        DocumentModel dm = new DocumentModel(texto);
        
        String nombre = file.getName();
        String base = nombre.endsWith(".txt") ? nombre.substring(0, nombre.length() - 4) : nombre;
        File html = new File(file.getParentFile(), base + ".html");
        if (!html.exists()) {
            return dm;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(html), StandardCharsets.UTF_8))) {
            String first = br.readLine();
            if (first != null && first.startsWith("DEFAULT;")) {
                String[] ds = first.split(";", -1);
                if (ds.length >= 5) {
                    dm.setDefaultFont(ds[1]);
                    try {
                        dm.setDefaultSize(Integer.parseInt(ds[2]));
                    } catch (Exception ex) {
                    }
                    try {
                        dm.setDefaultStyle(Integer.parseInt(ds[3]));
                    } catch (Exception ex) {
                    }
                    dm.setDefaultColor(ds[4]);
                }
            }

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                FormatoTexto ft = FormatoTexto.fromString(linea);
                if (ft != null) {
                    dm.AgregarFormato(ft);
                }
            }
        }
        dm.normalizeFormato();
        return dm;
    }

    public static void guardarTxt(File archivo, DocumentModel txt) throws IOException {
        if (archivo == null || txt == null) {
            throw new IllegalArgumentException();
        }
        try (Writer w = new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8)) {
            w.write(txt.getTexto());
        }

        String nombre = archivo.getName();
        String base = nombre.endsWith(".txt") ? nombre.substring(0, nombre.length() - 4) : nombre;
        File html = new File(archivo.getParentFile(), base + ".html");
  
        try (Writer w = new OutputStreamWriter(new FileOutputStream(html), StandardCharsets.UTF_8)) {
            w.write("DEFAULT;" + guardarTxt(txt.getDefaultFont()) + ";" + txt.getDefaultSize() + ";" + txt.getDefaultStyle() + ";" + txt.getDefaultColor() + "\n");
            List<FormatoTexto> rs = txt.getFormato();
            for (FormatoTexto r : rs) {
                w.write(r.toString() + "\n");
            }
        }
    }

    private static String readAll(File archivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            String linea;
            boolean first = true;
            while ((linea = br.readLine()) != null) {
                if (!first) {
                    sb.append(System.lineSeparator());
                }
                sb.append(linea);
                first = false;
            }
        }
        return sb.toString();
    }

    private static String guardarTxt(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }
}
