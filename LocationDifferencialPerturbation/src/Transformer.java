//import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


// �������������ϲ���չ��Ϊƽ�����꣬��˹-����������ϵ�ǽ������������չ��Ϊ��ƽ������ϵ�������߽�ÿ��������Ϊһ������������ϵ  
// ������Ŀǰֻ��Ա�����  
// ������B~[39.4, 41.0] L~[115.4, 117.5] L0=116  

public class Transformer {
	// private static final Logger LOGGER = LoggerFactory
	// .getLogger(Transformer.class);
	// private static int Datum = 84; // ����ϵ��Ĭ��ΪWGS84

	// DecimalFormat dGPS = new DecimalFormat("0.000000");
	// DecimalFormat dXY = new DecimalFormat("0");

	/* �ο������������ */
	private double a; // ������
	private double f; // ���� f=(a-b)/a
	private double b; // �̰��� b=(1-f)a
	private double e; // ��һƫ����
	private double e1; // �ڶ�ƫ����

	private double L0; // ���뾭��
	private double W0; // ԭ��ά��
	private double k0; // ��������
	private double FE; // ��ƫ��
	private double FN; // ��ƫ��

	double PI = 3.141592653589794;
	double iPI = 0.0174532925199433; // PI/180

	public Transformer(String city) {
		System.out.println(city + " map is loading");
		// Datum ͶӰ��׼�����ͣ�����54��׼��Ϊ54������80��׼��Ϊ80��WGS84��׼��Ϊ84
		/*
		 * if (Datum == 84) { a = 6378137; f = 1 / 298.257223563; } else if
		 * (Datum == 54) { a = 6378245; f = 1 / 298.3; } else if (Datum == 80) {
		 * a = 6378140; f = 1 / 298.257; } else { // ������������WGS84��׼��Ϊ84���� a =
		 * 6378137; f = 1 / 298.257223563; }
		 */
		a = 6378245;
		f = 1 / 298.3;
		b = (1 - f) * a;
		e = Math.sqrt(2 * f - f * f);
		e1 = e / Math.sqrt(1 - e * e);

		L0 = 116; // ��ʱ����
		W0 = 0; //
		k0 = 1; //
		FE = 0; //
		FN = 0; //

	}

	// ����
	public List<PlaneCoordinate> getXY(List<GpsCoordinate> OriginalGps) {
		List<PlaneCoordinate> PerturbXY = new ArrayList<PlaneCoordinate>();
		int length = OriginalGps.size();
		System.out.println("ԭʼ:"+length);
		for (int i = 0; i < length; i++) {
			double latitude = OriginalGps.get(i).getLat(); // γ�� B W
			double longitude = OriginalGps.get(i).getLong(); // ���� L J
			/* ��Ҫ����׼�� */
			double B = (latitude - W0) * iPI; // γ���
			double L = (longitude - L0) * iPI; // �����
			double sinB = Math.sin(B);
			double cosB = Math.cos(B);
			double tanB = Math.tan(B);
			double N = a / Math.sqrt(1 - Math.pow(e * sinB, 2)); // î��Ȧ���ʰ뾶
			double g = e1 * cosB;

			/* ������s */
			double s; // �����γ��latitude�ľ��߻���
			double B0;
			double B2;
			double B4;
			double B6;
			double B8;
			double C = Math.pow(a, 2) / b;
			B0 = 1 - 3.0 / 4.0 * Math.pow(e1, 2) + 45.0 / 64.0
					* Math.pow(e1, 4) - 175.0 / 256.0 * Math.pow(e1, 6)
					+ 11025.0 / 16384.0 * Math.pow(e1, 8);
			B2 = B0 - 1;
			B4 = 15.0 / 32.0 * Math.pow(e1, 4) - 175.0 / 384.0
					* Math.pow(e1, 6) + 3675.0 / 8192.0 * Math.pow(e1, 8);
			B6 = 0 - 35.0 / 96.0 * Math.pow(e1, 6) + 735.0 / 2048.0
					* Math.pow(e1, 8);
			B8 = 315.0 / 1024.0 * Math.pow(e1, 8);
			s = C
					* (B0 * B + sinB
							* (B2 * cosB + B4 * Math.pow(cosB, 3) + B6
									* Math.pow(cosB, 5) + B8
									* Math.pow(cosB, 7)));

			/* ���ƽ��ֱ������ϵ���� */
			double xTemp = s
					+ Math.pow(L, 2)
					* N
					* sinB
					* cosB
					/ 2.0
					+ Math.pow(L, 4)
					* N
					* sinB
					* Math.pow(cosB, 3)
					* (5 - Math.pow(tanB, 2) + 9 * Math.pow(g, 2) + 4 * Math
							.pow(g, 4)) / 24.0 + Math.pow(L, 6) * N * sinB
					* Math.pow(cosB, 5)
					* (61 - 58 * Math.pow(tanB, 2) + Math.pow(tanB, 4)) / 720.0;
			double yTemp = L
					* N
					* cosB
					+ Math.pow(L, 3)
					* N
					* Math.pow(cosB, 3)
					* (1 - Math.pow(tanB, 2) + Math.pow(g, 2))
					/ 6.0
					+ Math.pow(L, 5)
					* N
					* Math.pow(cosB, 5)
					* (5 - 18 * Math.pow(tanB, 2) + Math.pow(tanB, 4) + 14
							* Math.pow(g, 2) - 58 * Math.pow(g, 2)
							* Math.pow(tanB, 2)) / 120.0;
			PlaneCoordinate xy = new PlaneCoordinate();
			xy.setX(xTemp + FN);
			xy.setY(yTemp + FE);
			PerturbXY.add(xy);
		}
		return PerturbXY;
	}

	// ����
	public List<GpsCoordinate> getGPS(List<PlaneCoordinate> l) {
		List<GpsCoordinate> PerturbGPS = new ArrayList<GpsCoordinate>();
		int length = l.size();
		for (int i = 0; i < length; i++) {
			double x = l.get(i).getX();
			double y = l.get(i).getY();

			double El1 = (1 - Math.sqrt(1 - Math.pow(e, 2)))
					/ (1 + Math.sqrt(1 - Math.pow(e, 2)));
			double Mf = (x - FN) / k0; // ��ʵ����ֵ
			double Q = Mf
					/ (a * (1 - Math.pow(e, 2) / 4.0 - 3 * Math.pow(e, 4)
							/ 64.0 - 5 * Math.pow(e, 6) / 256.0));
			double Bf = Q
					+ (3 * El1 / 2.0 - 27 * Math.pow(El1, 3) / 32.0)
					* Math.sin(2 * Q)
					+ (21 * Math.pow(El1, 2) / 16.0 - 55 * Math.pow(El1, 4) / 32.0)
					* Math.sin(4 * Q) + (151 * Math.pow(El1, 3) / 96.0)
					* Math.sin(6 * Q) + 1097 / 512.0 * Math.pow(El1, 4)
					* Math.sin(8 * Q);
			double sinBf = Math.sin(Bf);
			double tanBf = Math.tan(Bf);
			double cosBf = Math.cos(Bf);
			double Rf = a * (1 - Math.pow(e, 2))
					/ Math.sqrt(Math.pow(1 - Math.pow(e * sinBf, 2), 3));
			double Nf = a / Math.sqrt(1 - Math.pow(e * sinBf, 2)); // î��Ȧ���ʰ뾶
			double Tf = Math.pow(tanBf, 2);
			double D = (y - FE) / (k0 * Nf);
			double Cf = Math.pow(e1, 2) * Math.pow(cosBf, 2);

			double B = Bf
					- Nf
					* tanBf
					/ Rf
					* (Math.pow(D, 2)
							/ 2.0
							- (5 + 3 * Tf + 10 * Cf - 9 * Tf * Cf - 4
									* Math.pow(Cf, 2) - 9 * Math.pow(e1, 2))
							* Math.pow(D, 4) / 24.0 + (61 + 90 * Tf + 45
							* Math.pow(Tf, 2) - 256 * Math.pow(e1, 2) - 3 * Math
							.pow(Cf, 2)) * Math.pow(D, 6) / 720.0);
			double L = L0
					* iPI
					+ 1
					/ cosBf
					* (D - (1 + 2 * Tf + Cf) * Math.pow(D, 3) / 6.0 + (5 - 2
							* Cf + 28 * Tf - 3 * Math.pow(Cf, 2) + 8
							* Math.pow(e1, 2) + 24 * Math.pow(Tf, 2))
							* Math.pow(D, 5) / 120.0);

			double Bangle = B / iPI;
			double Langle = L / iPI;

			GpsCoordinate gps = new GpsCoordinate();
			gps.setLat(Bangle + W0);
			gps.setLong(Langle);
			//System.out.println("��"+i+":"+gps);

			PerturbGPS.add(gps);
		}
		return PerturbGPS;
	}

}