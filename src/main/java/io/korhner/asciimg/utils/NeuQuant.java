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
	   [select sampleFac in range 1..30]
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

	/* defs for freq and bias */
	/** bias for fractions */
	protected static final int INT_BIAS_SHIFT = 16;
	protected static final int INT_BIAS = (((int) 1) << INT_BIAS_SHIFT);
	/** GAMMA = 1024 */
	protected static final int GAMMA_SHIFT = 10;
	protected static final int GAMMA = (((int) 1) << GAMMA_SHIFT);
	protected static final int BETA_SHIFT = 10;
	/** BETA = 1/1024 */
	protected static final int BETA = (INT_BIAS >> BETA_SHIFT);
	protected static final int BETA_GAMMA = (INT_BIAS << (GAMMA_SHIFT - BETA_SHIFT));

	/* defs for decreasing radius factor */
	/** for 256 cols, radius starts */
	protected static final int INIT_RAD = (NET_SIZE >> 3);
	/** at 32.0 biased by 6 bits */
	protected static final int RADIUS_BIAS_SHIFT = 6;
	protected static final int RADIUS_BIAS = (((int) 1) << RADIUS_BIAS_SHIFT);
	/** and decreases by a */
	protected static final int INIT_RADIUS = (INIT_RAD * RADIUS_BIAS);
	/** factor of 1/30 each cycle */
	protected static final int RADIUS_DEC = 30;

	/* defs for decreasing alpha factor */
	/** alpha starts at 1.0 */
	protected static final int ALPHA_BIAS_SHIFT = 10;
	protected static final int INIT_ALPHA = (((int) 1) << ALPHA_BIAS_SHIFT);

	/** biased by 10 bits */
	protected int alphaDec;

	/* RAD_BIAS and ALPHA_RAD_BIAS used for radPower calculation */
	protected static final int RAD_BIAS_SHIFT = 8;
	protected static final int RAD_BIAS = (((int) 1) << RAD_BIAS_SHIFT);
	protected static final int ALPHA_RAD_B_SHIFT = (ALPHA_BIAS_SHIFT + RAD_BIAS_SHIFT);
	protected static final int ALPHA_RAD_BIAS = (((int) 1) << ALPHA_RAD_B_SHIFT);

	/* Types and Global Variables
	-------------------------- */

	/** the input image itself */
	protected byte[] picture;
	/** lengthCount = H*W*3 */
	protected int lengthCount;

	/** sampling factor 1..30 */
	protected int sampleFac;

	///** BGRc */
	//typedef int pixel[4];
	/** the network itself - [NET_SIZE][4] */
	protected int[][] network;

	/** for network lookup - really 256 */
	protected int[] netIndex = new int[256];

	/** bias and freq arrays for learning */
	protected int[] bias = new int[NET_SIZE];
	protected int[] freq = new int[NET_SIZE];
	/** radPower for pre-computation */
	protected int[] radPower = new int[INIT_RAD];

	/**
	 * Initialise network in range (0,0,0) to (255,255,255) and set parameters
	 */
	public NeuQuant(final byte[] picture, final int lengthCount, final int sampleFac) {

		int i;
		int[] p;

		this.picture = picture;
		this.lengthCount = lengthCount;
		this.sampleFac = sampleFac;

		network = new int[NET_SIZE][];
		for (i = 0; i < NET_SIZE; i++) {
			network[i] = new int[4];
			p = network[i];
			Arrays.fill(p, 0, 3, (i << (NET_BIAS_SHIFT + 8)) / NET_SIZE);
			freq[i] = INT_BIAS / NET_SIZE; /* 1/NET_SIZE */
			bias[i] = 0;
		}
	}

	public byte[] colorMap() {
		byte[] map = new byte[3 * NET_SIZE];
		int[] index = new int[NET_SIZE];
		for (int i = 0; i < NET_SIZE; i++)
			index[network[i][3]] = i;
		int k = 0;
		for (int i = 0; i < NET_SIZE; i++) {
			int j = index[i];
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

		int i, j, smallPos, smallVal;
		int[] p;
		int[] q;
		int previousCol, startPos;

		previousCol = 0;
		startPos = 0;
		for (i = 0; i < NET_SIZE; i++) {
			p = network[i];
			smallPos = i;
			smallVal = p[1]; /* index on g */
			/* find smallest in i..NET_SIZE-1 */
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
			/* smallVal entry is now in position i */
			if (smallVal != previousCol) {
				netIndex[previousCol] = (startPos + i) >> 1;
				for (j = previousCol + 1; j < smallVal; j++)
					netIndex[j] = i;
				previousCol = smallVal;
				startPos = i;
			}
		}
		netIndex[previousCol] = (startPos + MAX_NET_POS) >> 1;
		for (j = previousCol + 1; j < 256; j++)
			netIndex[j] = MAX_NET_POS; /* really 256 */
	}

	/**
	 * Main Learning Loop
	 */
	public void learn() {

		int i, j, b, g, r;
		int radius, rad, alpha, step, delta, samplePixels;
		byte[] p;
		int pix, lim;

		if (lengthCount < MIN_PICTURE_BYTES)
			sampleFac = 1;
		alphaDec = 30 + ((sampleFac - 1) / 3);
		p = picture;
		pix = 0;
		lim = lengthCount;
		samplePixels = lengthCount / (3 * sampleFac);
		delta = samplePixels / N_CYCLES;
		alpha = INIT_ALPHA;
		radius = INIT_RADIUS;

		rad = radius >> RADIUS_BIAS_SHIFT;
		if (rad <= 1)
			rad = 0;
		for (i = 0; i < rad; i++)
			radPower[i] =
				alpha * (((rad * rad - i * i) * RAD_BIAS) / (rad * rad));

		//fprintf(stderr,"beginning 1D learning: initial radius=%d\n", rad);

		if (lengthCount < MIN_PICTURE_BYTES)
			step = 3;
		else if ((lengthCount % PRIME_1) != 0)
			step = 3 * PRIME_1;
		else {
			if ((lengthCount % PRIME_2) != 0)
				step = 3 * PRIME_2;
			else {
				if ((lengthCount % PRIME_3) != 0)
					step = 3 * PRIME_3;
				else
					step = 3 * PRIME_4;
			}
		}

		i = 0;
		while (i < samplePixels) {
			b = (p[pix + 0] & 0xff) << NET_BIAS_SHIFT;
			g = (p[pix + 1] & 0xff) << NET_BIAS_SHIFT;
			r = (p[pix + 2] & 0xff) << NET_BIAS_SHIFT;
			j = contest(b, g, r);

			alterSingle(alpha, j, b, g, r);
			if (rad != 0)
				alterNeigh(rad, j, b, g, r); /* alter neighbours */

			pix += step;
			if (pix >= lim)
				pix -= lengthCount;

			i++;
			if (delta == 0)
				delta = 1;
			if (i % delta == 0) {
				alpha -= alpha / alphaDec;
				radius -= radius / RADIUS_DEC;
				rad = radius >> RADIUS_BIAS_SHIFT;
				if (rad <= 1)
					rad = 0;
				for (j = 0; j < rad; j++)
					radPower[j] =
						alpha * (((rad * rad - j * j) * RAD_BIAS) / (rad * rad));
			}
		}
		//fprintf(stderr,"finished 1D learning: final alpha=%f !\n",((float)alpha)/INIT_ALPHA);
	}

	/**
	 * Search for BGR values 0..255 (after net is unbiased) and return colour index
	 */
	public int map(final int b, final int g, final int r) {

		int i, j, dist, a, bestD;
		int[] p;
		int best;

		bestD = 1000; /* biggest possible dist is 256*3 */
		best = -1;
		i = netIndex[g]; /* index on g */
		j = i - 1; /* start at netIndex[g] and work outwards */

		while ((i < NET_SIZE) || (j >= 0)) {
			if (i < NET_SIZE) {
				p = network[i];
				dist = p[1] - g; /* inx key */
				if (dist >= bestD)
					i = NET_SIZE; /* stop loop */
				else {
					i++;
					if (dist < 0)
						dist = -dist;
					a = p[0] - b;
					if (a < 0)
						a = -a;
					dist += a;
					if (dist < bestD) {
						a = p[2] - r;
						if (a < 0)
							a = -a;
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
				dist = g - p[1]; /* inx key - reverse dif */
				if (dist >= bestD)
					j = -1; /* stop loop */
				else {
					j--;
					if (dist < 0)
						dist = -dist;
					a = p[0] - b;
					if (a < 0)
						a = -a;
					dist += a;
					if (dist < bestD) {
						a = p[2] - r;
						if (a < 0)
							a = -a;
						dist += a;
						if (dist < bestD) {
							bestD = dist;
							best = p[3];
						}
					}
				}
			}
		}
		return (best);
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

		int i, j;

		for (i = 0; i < NET_SIZE; i++) {
			network[i][0] >>= NET_BIAS_SHIFT;
			network[i][1] >>= NET_BIAS_SHIFT;
			network[i][2] >>= NET_BIAS_SHIFT;
			network[i][3] = i; /* record colour no */
		}
	}

	/**
	 * Move adjacent neurons by precomputed alpha*(1-((i-j)^2/[r]^2)) in radPower[|i-j|]
	 */
	protected void alterNeigh(final int rad, final int i, final int b, final int g, final int r) {

		int j, k, lo, hi, a, m;
		int[] p;

		lo = i - rad;
		if (lo < -1)
			lo = -1;
		hi = i + rad;
		if (hi > NET_SIZE)
			hi = NET_SIZE;

		j = i + 1;
		k = i - 1;
		m = 1;
		while ((j < hi) || (k > lo)) {
			a = radPower[m++];
			if (j < hi) {
				p = network[j++];
				try {
					p[0] -= (a * (p[0] - b)) / ALPHA_RAD_BIAS;
					p[1] -= (a * (p[1] - g)) / ALPHA_RAD_BIAS;
					p[2] -= (a * (p[2] - r)) / ALPHA_RAD_BIAS;
				} catch (Exception e) {
				} // prevents 1.3 miscompilation
			}
			if (k > lo) {
				p = network[k--];
				try {
					p[0] -= (a * (p[0] - b)) / ALPHA_RAD_BIAS;
					p[1] -= (a * (p[1] - g)) / ALPHA_RAD_BIAS;
					p[2] -= (a * (p[2] - r)) / ALPHA_RAD_BIAS;
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Move neuron i towards biased (b,g,r) by factor alpha
	 */
	protected void alterSingle(final int alpha, final int i, final int b, final int g, final int r) {

		/* alter hit neuron */
		int[] n = network[i];
		n[0] -= (alpha * (n[0] - b)) / INIT_ALPHA;
		n[1] -= (alpha * (n[1] - g)) / INIT_ALPHA;
		n[2] -= (alpha * (n[2] - r)) / INIT_ALPHA;
	}

	/**
	 * Search for biased BGR values
	 */
	protected int contest(final int b, final int g, final int r) {

		/* finds closest neuron (min dist) and updates freq */
		/* finds best neuron (min dist-bias) and returns position */
		/* for frequently chosen neurons, freq[i] is high and bias[i] is negative */
		/* bias[i] = GAMMA*((1/NET_SIZE)-freq[i]) */

		int i, dist, a, biasDist, betaFreq;
		int bestPos, bestBiasPos, bestD, bestBiasd;
		int[] n;

		bestD = ~(((int) 1) << 31);
		bestBiasd = bestD;
		bestPos = -1;
		bestBiasPos = bestPos;

		for (i = 0; i < NET_SIZE; i++) {
			n = network[i];
			dist = n[0] - b;
			if (dist < 0)
				dist = -dist;
			a = n[1] - g;
			if (a < 0)
				a = -a;
			dist += a;
			a = n[2] - r;
			if (a < 0)
				a = -a;
			dist += a;
			if (dist < bestD) {
				bestD = dist;
				bestPos = i;
			}
			biasDist = dist - ((bias[i]) >> (INT_BIAS_SHIFT - NET_BIAS_SHIFT));
			if (biasDist < bestBiasd) {
				bestBiasd = biasDist;
				bestBiasPos = i;
			}
			betaFreq = (freq[i] >> BETA_SHIFT);
			freq[i] -= betaFreq;
			bias[i] += (betaFreq << GAMMA_SHIFT);
		}
		freq[bestPos] += BETA;
		bias[bestPos] -= BETA_GAMMA;
		return (bestBiasPos);
	}
}
