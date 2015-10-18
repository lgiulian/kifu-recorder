package br.edu.ifspsaocarlos.sdm.kifurecorder.processamento;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.kifurecorder.MainActivity;
import br.edu.ifspsaocarlos.sdm.kifurecorder.jogo.Tabuleiro;

/**
 * Created by leo on 07/10/15.
 *
 * Classe responsável por colocar a imagem de saída nas matrizes de imagem.
 */
public class Desenhista {

    private static Scalar mBlack = new Scalar(  0,   0,   0);
    private static Scalar mWhite = new Scalar(255, 255, 255);
    private static Scalar mRed   = new Scalar(255,   0,   0);
    private static Scalar mGreen = new Scalar(  0, 255,   0);
    private static Scalar mBlue  = new Scalar(  0,   0, 255);

    private static Scalar[] colors = new Scalar[] {
            mRed, mGreen, mBlue, mWhite
    };

    public static void desenharContornosRelevantes(Mat imagem, HierarquiaDeQuadrilateros hierarquiaDeQuadrilateros, MatOfPoint contornoMaisProximoDoTabuleiro) {
        // Desenha os quadriláteros folha em verde
        if (hierarquiaDeQuadrilateros.folhas.size() > 0) {
            Imgproc.drawContours(imagem, hierarquiaDeQuadrilateros.folhas, -1, mGreen);
        }

        // Desenha os quadriláteros externos em azul
        if (hierarquiaDeQuadrilateros.externos.size() > 0) {
            Imgproc.drawContours(imagem, hierarquiaDeQuadrilateros.externos, -1, mBlue);
        }

        // Desenha o contorno do tabuleiro em vermelho
        if (contornoMaisProximoDoTabuleiro != null) {
            desenharContorno(imagem, contornoMaisProximoDoTabuleiro, mRed);
        }
    }

    private static void desenharContorno(Mat imagem, MatOfPoint contorno, Scalar cor) {
        List<MatOfPoint> listaContorno = new ArrayList<MatOfPoint>();
        listaContorno.add(contorno);
        Imgproc.drawContours(imagem, listaContorno, -1, cor);
    }

    public static void desenhaInterseccoesECantosDoTabuleiro(Mat imagem, List<ClusterDeVertices> intersecoes, List<Point> cantos) {
        // Desenha as interseções encontradas
        for (ClusterDeVertices c : intersecoes) {
            Core.circle(imagem, c.verticeMedio(), 10, mWhite, 2);
        }

        // Desenha os 4 cantos do quadrilátero do tabuleiro
        for (int i = 0; i < 4; ++i) {
            Log.d(MainActivity.TAG, "Canto " + i + ": " + cantos.get(i));
            Core.circle(imagem, cantos.get(i), 10, colors[i], -1);
        }
    }

    public static void desenhaInterseccoes(Mat imagem, List<ClusterDeVertices> intersecoes) {
        for (ClusterDeVertices c : intersecoes) {
            Core.circle(imagem, c.verticeMedio(), 10, mWhite, 2);
        }
    }

    public static void desenhaLinhasNoPreview(Mat imagemTabuleiroCorrigido, int larguraImagemPreview,
                                              int alturaImagemPreview) {
        // Desenha linhas do tabuleiro na imagem de preview
        Core.line(imagemTabuleiroCorrigido, new Point(0, 0), new Point(larguraImagemPreview, alturaImagemPreview), mGreen);
        Core.line(imagemTabuleiroCorrigido, new Point(larguraImagemPreview, 0), new Point(0, alturaImagemPreview), mGreen);
        for (int i = 0; i < 9; ++i) {
            Core.line(imagemTabuleiroCorrigido, new Point(0, i * alturaImagemPreview / 8), new Point(larguraImagemPreview, i * alturaImagemPreview / 8), mBlue);
        }
        for (int i = 0; i < 9; ++i) {
            Core.line(imagemTabuleiroCorrigido, new Point(i * larguraImagemPreview / 8, 0), new Point(i * larguraImagemPreview / 8, alturaImagemPreview), mBlue);
        }
    }

    /**
     * Desenha o tabuleiro sobre a matriz 'imagem' com a origem nas coordenadas 'x' e 'y' passadas
     * como parâmetro e com tamanho 'tamanhoImagem'.
     *
     * @param imagem
     * @param tabuleiro
     * @param x
     * @param y
     * @param tamanhoImagem
     */
    public static void desenharTabuleiro(Mat imagem, Tabuleiro tabuleiro, int x, int y, int tamanhoImagem) {
        Point p1 = new Point();
        Point p2 = new Point();
        p1.x = x;
        p1.y = y;
        p2.x = x + tamanhoImagem;
        p2.y = y + tamanhoImagem;

        Core.rectangle(imagem, p1, p2, mWhite, -1);

        for (int i = 0; i < tabuleiro.getDimensao(); ++i) {
            Point inicio = new Point();
            Point fim = new Point();
            inicio.x = x + (tamanhoImagem / 10);
            inicio.y = y + (tamanhoImagem / 10) + (tamanhoImagem / 10) * i;
            fim.x = x + (tamanhoImagem * 0.9);
            fim.y = y + (tamanhoImagem / 10) + (tamanhoImagem / 10) * i;
            Core.line(imagem, inicio, fim, mBlack);
        }

        for (int i = 0; i < tabuleiro.getDimensao(); ++i) {
            Point inicio = new Point();
            Point fim = new Point();
            inicio.x = x + (tamanhoImagem / 10) + (tamanhoImagem / 10) * i;
            inicio.y = y + (tamanhoImagem / 10);
            fim.x = x + (tamanhoImagem / 10) + (tamanhoImagem / 10) * i;
            fim.y = y + (tamanhoImagem * 0.9);
            Core.line(imagem, inicio, fim, mBlack);
        }

        for (int i = 0; i < tabuleiro.getDimensao(); ++i) {
            for (int j = 0; j < tabuleiro.getDimensao(); ++j) {
                Point centro = new Point();
                centro.x = x + (tamanhoImagem / 10) + i * (tamanhoImagem / 10);
                centro.y = y + (tamanhoImagem / 10) + j * (tamanhoImagem / 10);
                if (tabuleiro.getPosicao(i, j) == Tabuleiro.PEDRA_PRETA) {
                    Core.circle(imagem, centro, (tamanhoImagem / 20), mBlack, -1);
                } else if (tabuleiro.getPosicao(i, j) == Tabuleiro.PEDRA_BRANCA) {
                    Core.circle(imagem, centro, (tamanhoImagem / 20), mWhite, -1);
                    Core.circle(imagem, centro, (tamanhoImagem / 20), mBlack);
                }
            }
        }

    }

}