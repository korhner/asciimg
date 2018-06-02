/* NeuQuant Neural-Net Quantization Algorithm
 * ------------------------------------------
 *
 * Copyright (c) 1994 Anthony Dekker
 *
 * NEUQUANT Neural-Net quantization algorithm by Anthony Dekker, 1994.
 * See "Kohonen neural networks for optimal colour quantization"
 * in "Network: Computation in Neural Systems" Vol. 5 (1994) pp 351-367.
 * for a discussion of the algorithm.
 *
 * Any party obtaining a copy of these files from the author, directly or
 * indirectly, is granted, free of charge, a full and unrestricted irrevocable,
 * world-wide, paid up, royalty-free, nonexclusive right and license to deal
 * in this software and documentation files (the "Software"), including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons who receive
 * copies from any such party to do so, with the only requirement being
 * that this copyright notice remain intact.
 */

// Ported to Java 12/00 K Weiner

package io.korhner.asciimg.utils;

import java.util.Arrays;

public class NeuQuant {

	/** number of colours used */
	protected static final int NET_SIZE = 256;

	/*
	four primes near 500 - assume no image has a length so large
	that it is divisible by all four primes
	*/
	protected static final int PRIME_1 = 499;
	protected static final int PRIME_2 = 491;
	protected static final int PRIME_3 = 487;
	protected static final int PRIME_4 = 503;

	/** minimum size for input image */
	protected static final int MIN_PICTURE_BYTES = (3 * PRIME_4);

	/* Program Skeleton
	   ----------------
	   [select sampleFac input range 1..30]
	   [read image from input file]
	   pic = (unsigned char*) malloc(3*width*height);
	   initNet(pic,3*width*height,sampleFac);
	   learn();
	   unbiasNet();
	   [write output image header, using writeColourMap(f)]
	   inXBuild();
	   write output image using inXSearch(b,g,r)      */

	/* Network Definitions
	   ------------------- */

	protected static final int MAX_NET_POS = (NET_SIZE - 1);
	/** bias for colour values */
	protected static final int NET_BIAS_SHIFT = 4;
	/** no. of learning cycles */
	protected static final int N_CYCLES = 100;

	/* definitions for freq and bias */
	/** bias for fractions */
	protected static final int INT_BIAS_SHIFT = 16;
	protected static final int INT_BIAS = (1 << INT_BIAS_SHIFT);
	/** GAMMA = 1024 */
	protected static final int GAMMA_SHIFT = 10;
	protected static final int GAMMA = (1 << GAMMA_SHIFT);
	protected static final int BETA_SHIFT = 10;
	/** BETA = 1/1024 */
	protected static final int BETA = (INT_BIAS >> BETA_SHIFT);
	protected static final int BETA_GAMMA = (INT_BIAS << (GAMMA_SHIFT - BETA_SHIFT));

	/* definitions for decreasing radius factor */
	/** for 256 cols, radius starts */
	protected static final int INIT_RAD = (NET_SIZE >> 3);
	/** at 32.0 biased by 6 bits */
	protected static final int RADIUS_BIAS_SHIFT = 6;
	protected static final int RADIUS_BIAS = (1 << RADIUS_BIAS_SHIFT);
	/** and decreases by a */
	protected static final int INIT_RADIUS = (INIT_RAD * RADIUS_BIAS);
	/** factor of 1/30 each cycle */
	protected static final int RADIUS_DEC = 30;

	/* definitions for decreasing alpha factor */
	/** alpha starts at 1.0 */
	protected static final int ALPHA_BIAS_SHIFT = 10;
	protected static final int INIT_ALPHA = (1 << ALPHA_BIAS_SHIFT);

	/** biased by 10 bits */
	protected int alphaDec;

	/* RAD_BIAS and ALPHA_RAD_BIAS used for radPower calculation */
	protected static final int RAD_BIAS_SHIFT = 8;
	protected static final int RAD_BIAS = (1 << RAD_BIAS_SHIFT);
	protected static final int ALPHA_RAD_B_SHIFT = (ALPHA_BIAS_SHIFT + RAD_BIAS_SHIFT);
	protected static final int ALPHA_RAD_BIAS = (1 << ALPHA_RAD_B_SHIFT);

	/* Types and Global Variables
	-------------------------- */

	/** the input image itself */
	protected final byte[] picture;
	/** lengthCount = H*W*3 */
	protected final int lengthCount;

	/** sampling factor 1..30 */
	protected int sampleFac;

	///** BGRc */
	//typedef int pixel[4];
	/** the network itself - [NET_SIZE][4] */
	protected final int[][] network;

	/** for network lookup - really 256 */
	protected final int[] netIndex;

	/** bias and freq arrays for learning */
	protected final int[] bias;
	protected final int[] freq;
	/** radPower for pre-computation */
	protected final int[] radPower;

	/**
	 * Initialise network input range (0,0,0) to (255,255,255) and set parameters
	 *
	 * @param picture the input image itself
	 * @param lengthCount H*W*3
	 * @param sampleFac sampling factor 1..30
	 */
	public NeuQuant(final byte[] picture, final int lengthCount, final int sampleFac) {

		this.picture = picture;
		this.lengthCount = lengthCount;
		this.sampleFac = sampleFac;

		network = new int[NET_SIZE][];
		bias = new int[NET_SIZE];
		freq = new int[NET_SIZE];
		for (int i = 0; i < NET_SIZE; i++) {
			network[i] = new int[4];
			final int[] p = network[i];
			Arrays.fill(p, 0, 3, (i << (NET_BIAS_SHIFT + 8)) / NET_SIZE);
			freq[i] = INT_BIAS / NET_SIZE; /* 1/NET_SIZE */
			bias[i] = 0;
		}
		netIndex = new int[256];
		radPower = new int[INIT_RAD];
	}

	public byte[] colorMap() {
		final byte[] map = new byte[3 * NET_SIZE];
		final int[] index = new int[NET_SIZE];
		for (int i = 0; i < NET_SIZE; i++) {
			index[network[i][3]] = i;
		}
		int k = 0;
		for (int i = 0; i < NET_SIZE; i++) {
			final int j = index[i];
			map[k++] = (byte) (network[j][0]);
			map[k++] = (byte) (network[j][1]);
			map[k++] = (byte) (network[j][2]);
		}
		return map;
	}

	/**
	 * Insertion sort of network and building of netIndex[0..255] (to do after unbias)
	 */
	public void inXBuild() {

		int i;
		int j;
		int[] p;
		int[] q;
		int previousCol = 0;
		int startPos = 0;
		for (i = 0; i < NET_SIZE; i++) {
			p = network[i];
			int smallPos = i;
			int smallVal = p[1]; /* index on g */
			/* find smallest input i..NET_SIZE-1 */
			for (j = i + 1; j < NET_SIZE; j++) {
				q = network[j];
				if (q[1] < smallVal) { /* index on g */
					smallPos = j;
					smallVal = q[1]; /* index on g */
				}
			}
			q = network[smallPos];
			/* swap p (i) and q (smallPos) entries */
			if (i != smallPos) {
				j = q[0];
				q[0] = p[0];
				p[0] = j;
				j = q[1];
				q[1] = p[1];
				p[1] = j;
				j = q[2];
				q[2] = p[2];
				p[2] = j;
				j = q[3];
				q[3] = p[3];
				p[3] = j;
			}
			/* smallVal entry is now input position i */
			if (smallVal != previousCol) {
				netIndex[previousCol] = (startPos + i) >> 1;
				for (j = previousCol + 1; j < smallVal; j++) {
					netIndex[j] = i;
				}
				previousCol = smallVal;
				startPos = i;
			}
		}
		netIndex[previousCol] = (startPos + MAX_NET_POS) >> 1;
		for (j = previousCol + 1; j < 256; j++) {
			netIndex[j] = MAX_NET_POS; /* really 256 */
		}
	}

	/**
	 * Main Learning Loop
	 */
	public void learn() {

		if (lengthCount < MIN_PICTURE_BYTES) {
			sampleFac = 1;
		}
		alphaDec = 30 + ((sampleFac - 1) / 3);
		final byte[] p = picture;
		int pix = 0;
		final int lim = lengthCount;
		final int samplePixels = lengthCount / (3 * sampleFac);
		int delta = samplePixels / N_CYCLES;
		int alpha = INIT_ALPHA;
		int radius = INIT_RADIUS;

		int rad = radius >> RADIUS_BIAS_SHIFT;
		if (rad <= 1) {
			rad = 0;
		}
		int i;
		for (i = 0; i < rad; i++) {
			radPower[i] = alpha * (((rad * rad - i * i) * RAD_BIAS) / (rad * rad));
		}

		//fprintf(stderr,"beginning 1D learning: initial radius=%d\n", rad);

		final int step;
		if (lengthCount < MIN_PICTURE_BYTES) {
			step = 3;
		} else {
			if ((lengthCount % PRIME_1) == 0) {
				if ((lengthCount % PRIME_2) == 0) {
					if ((lengthCount % PRIME_3) == 0) {
						step = 3 * PRIME_4;
					} else {
						step = 3 * PRIME_3;
					}
				} else {
					step = 3 * PRIME_2;
				}
			} else {
				step = 3 * PRIME_1;
			}
		}

		int j;
		i = 0;
		while (i < samplePixels) {
			final int blue  = (p[pix    ] & 0xff) << NET_BIAS_SHIFT;
			final int green = (p[pix + 1] & 0xff) << NET_BIAS_SHIFT;
			final int red   = (p[pix + 2] & 0xff) << NET_BIAS_SHIFT;
			j = contest(blue, green, red);

			alterSingle(alpha, j, blue, green, red);
			if (rad != 0) {
				alterNeigh(rad, j, blue, green, red); /* alter neighbours */
			}

			pix += step;
			if (pix >= lim) {
				pix -= lengthCount;
			}

			i++;
			if (delta == 0) {
				delta = 1;
			}
			if (i % delta == 0) {
				alpha -= alpha / alphaDec;
				radius -= radius / RADIUS_DEC;
				rad = radius >> RADIUS_BIAS_SHIFT;
				if (rad <= 1) {
					rad = 0;
				}
				for (j = 0; j < rad; j++) {
					radPower[j] = alpha * (((rad * rad - j * j) * RAD_BIAS) / (rad * rad));
				}
			}
		}
		//fprintf(stderr,"finished 1D learning: final alpha=%f !\n",((float)alpha)/INIT_ALPHA);
	}

	/**
	 * Search for BGR values (after net is unbiased).
	 *
	 * @param blue intensity of blue 0..255
	 * @param green intensity of green 0..255
	 * @param red intensity of red 0..255
	 * @return colour index
	 */
	public int map(final int blue, final int green, final int red) {

		int bestD = 1000; /* biggest possible dist is 256*3 */
		int best = -1;
		int i = netIndex[green]; /* index on green */
		int j = i - 1; /* start at netIndex[green] and work outwards */

		int a;
		int[] p;
		while ((i < NET_SIZE) || (j >= 0)) {
			int dist;
			if (i < NET_SIZE) {
				p = network[i];
				dist = p[1] - green; /* inx key */
				if (dist >= bestD) {
					i = NET_SIZE; /* stop loop */
				} else {
					i++;
					if (dist < 0) {
						dist = -dist;
					}
					a = p[0] - blue;
					if (a < 0) {
						a = -a;
					}
					dist += a;
					if (dist < bestD) {
						a = p[2] - red;
						if (a < 0) {
							a = -a;
						}
						dist += a;
						if (dist < bestD) {
							bestD = dist;
							best = p[3];
						}
					}
				}
			}
			if (j >= 0) {
				p = network[j];
				dist = green - p[1]; /* inx key - reverse dif */
				if (dist >= bestD) {
					j = -1; /* stop loop */
				} else {
					j--;
					if (dist < 0) {
						dist = -dist;
					}
					a = p[0] - blue;
					if (a < 0) {
						a = -a;
					}
					dist += a;
					if (dist < bestD) {
						a = p[2] - red;
						if (a < 0) {
							a = -a;
						}
						dist += a;
						if (dist < bestD) {
							bestD = dist;
							best = p[3];
						}
					}
				}
			}
		}
		return best;
	}

	public byte[] process() {
		learn();
		unbiasNet();
		inXBuild();
		return colorMap();
	}

	/**
	 * Unbias network to give byte values 0..255 and record position i to prepare for sort
	 */
	public void unbiasNet() {

		for (int i = 0; i < NET_SIZE; i++) {
			network[i][0] >>= NET_BIAS_SHIFT;
			network[i][1] >>= NET_BIAS_SHIFT;
			network[i][2] >>= NET_BIAS_SHIFT;
			network[i][3] = i; /* record colour no */
		}
	}

	/**
	 * Move adjacent neurons by precomputed <code>alpha*(1-((i-j)^2/[red]^2)) input radPower[|i-j|]</code>.
	 *
	 * @param rad TODO document this
	 * @param i index of the central neuron
	 * @param blue TODO document this
	 * @param green TODO document this
	 * @param red TODO document this
	 */
	protected void alterNeigh(final int rad, final int i, final int blue, final int green, final int red) {

		int j;
		int k;
		int low;
		int high;
		int a;
		int m;
		int[] neuron;

		low = i - rad;
		if (low < -1) {
			low = -1;
		}
		high = i + rad;
		if (high > NET_SIZE) {
			high = NET_SIZE;
		}

		j = i + 1;
		k = i - 1;
		m = 1;
		while ((j < high) || (k > low)) {
			a = radPower[m++];
			if (j < high) {
				neuron = network[j++];
				try {
					neuron[0] -= (a * (neuron[0] - blue)) / ALPHA_RAD_BIAS;
					neuron[1] -= (a * (neuron[1] - green)) / ALPHA_RAD_BIAS;
					neuron[2] -= (a * (neuron[2] - red)) / ALPHA_RAD_BIAS;
				} catch (final Exception exc) {
					// prevents 1.3 mis-compilation
				}
			}
			if (k > low) {
				neuron = network[k--];
				try {
					neuron[0] -= (a * (neuron[0] - blue)) / ALPHA_RAD_BIAS;
					neuron[1] -= (a * (neuron[1] - green)) / ALPHA_RAD_BIAS;
					neuron[2] -= (a * (neuron[2] - red)) / ALPHA_RAD_BIAS;
				} catch (final Exception exc) {
				}
			}
		}
	}

	/**
	 * Move neuron neuronIdx towards biased (blue,green,red) by factor alpha.
	 *
	 * @param alpha how much to move
	 * @param neuronIdx index of the neuron to be moved
	 * @param blue blue part of the bias
	 * @param green green part of the bias
	 * @param red red part of the bias
	 */
	protected void alterSingle(final int alpha, final int neuronIdx, final int blue, final int green, final int red) {

		/* alter hit neuron */
		final int[] neuron = network[neuronIdx];
		neuron[0] -= (alpha * (neuron[0] - blue)) / INIT_ALPHA;
		neuron[1] -= (alpha * (neuron[1] - green)) / INIT_ALPHA;
		neuron[2] -= (alpha * (neuron[2] - red)) / INIT_ALPHA;
	}

	/**
	 * Search for biased BGR values
	 *
	 * @param blue intensity of blue 0..255
	 * @param green intensity of green 0..255
	 * @param red intensity of red 0..255
	 * @return position of the best bias
	 */
	protected int contest(final int blue, final int green, final int red) {

		/* finds closest neuron (min dist) and updates freq */
		/* finds best neuron (min dist-bias) and returns position */
		/* for frequently chosen neurons, freq[i] is high and bias[i] is negative */
		/* bias[i] = GAMMA*((1/NET_SIZE)-freq[i]) */

		int a;

		@SuppressWarnings("NumericOverflow") int bestD = ~(1 << 31);
		int bestBiasD = bestD;
		int bestPos = -1;
		int bestBiasPos = bestPos;

		for (int i = 0; i < NET_SIZE; i++) {
			final int[] neuron = network[i];
			int dist = neuron[0] - blue;
			if (dist < 0) {
				dist = -dist;
			}
			a = neuron[1] - green;
			if (a < 0) {
				a = -a;
			}
			dist += a;
			a = neuron[2] - red;
			if (a < 0) {
				a = -a;
			}
			dist += a;
			if (dist < bestD) {
				bestD = dist;
				bestPos = i;
			}
			final int biasDist = dist - ((bias[i]) >> (INT_BIAS_SHIFT - NET_BIAS_SHIFT));
			if (biasDist < bestBiasD) {
				bestBiasD = biasDist;
				bestBiasPos = i;
			}
			final int betaFreq = (freq[i] >> BETA_SHIFT);
			freq[i] -= betaFreq;
			bias[i] += (betaFreq << GAMMA_SHIFT);
		}
		freq[bestPos] += BETA;
		bias[bestPos] -= BETA_GAMMA;
		return bestBiasPos;
	}
}
