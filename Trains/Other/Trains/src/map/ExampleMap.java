package map;

import java.util.HashSet;
import java.util.Set;

public final class ExampleMap {

    public static TrainsMap createExampleMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City LA = new City("LA", new Coord(.4f, .8f));
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d2 =
                new DirectConnection(SLO, SAC, 5, ColorTrains.WHITE);
        DirectConnection d3 = new DirectConnection(SLO, SF, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        connections.add(d1);
        connections.add(d2);
        //connections.add(d3);
        connections.add(d4);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    public static TrainsMap createExampleDisconnectedMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City LA = new City("LA", new Coord(.4f, .8f));
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City A = new City("A", new Coord(.7f, .8f));
        City B = new City("B", new Coord(.8f, .9f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        cities.add(A);
        cities.add(B);
        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d2 =
                new DirectConnection(SLO, SAC, 5, ColorTrains.WHITE);
        DirectConnection d3 = new DirectConnection(SLO, SF, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        DirectConnection d5 = new DirectConnection(A, B, 4, ColorTrains.WHITE);
        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    public static TrainsMap createSimpleDoubleConnectionMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City LA = new City("LA", new Coord(.8f, .4f));
        City SF = new City("SF", new Coord(.3f, .8f));
        cities.add(LA);
        cities.add(SF);
        DirectConnection d1 =
                new DirectConnection(LA, SF, 3, ColorTrains.GREEN);
        DirectConnection d2 = new DirectConnection(LA, SF, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(SF, LA, 4, ColorTrains.RED);
        connections.add(d1);
        connections.add(d2);
        connections.add(d4);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    public static TrainsMap createSimpleDoubleConnectionWithSameConnectionNames() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City LA = new City("LA", new Coord(.8f, .4f));
        City SF = new City("SF", new Coord(.3f, .8f));
        cities.add(LA);
        cities.add(SF);
        DirectConnection d2 = new DirectConnection(LA, SF, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(LA, SF, 5, ColorTrains.BLUE);
        DirectConnection d5 = new DirectConnection(LA, SF, 4, ColorTrains.RED);
        DirectConnection d1 = new DirectConnection(LA, SF, 4, ColorTrains.BLUE);

        connections.add(d1);
        connections.add(d2);
        connections.add(d4);
        connections.add(d5);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    public static TrainsMap createExampleMultipleConnectionMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City LA = new City("LA", new Coord(.4f, .8f));
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        cities.add(vegas);

        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d5 = new DirectConnection(LA, SLO, 5, ColorTrains.RED);

        DirectConnection d2 =
                new DirectConnection(SLO, SAC, 5, ColorTrains.WHITE);
        DirectConnection d3 = new DirectConnection(SLO, SF, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        DirectConnection d6 =
                new DirectConnection(SLO, vegas, 3, ColorTrains.BLUE);
        DirectConnection d7 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.BLUE);
        DirectConnection d8 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);
        DirectConnection d9 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.GREEN);
        DirectConnection d10 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.WHITE);
        DirectConnection d11 =
                new DirectConnection(vegas, SLO, 4, ColorTrains.BLUE);
        DirectConnection d12 =
                new DirectConnection(vegas, SLO, 4, ColorTrains.RED);
        DirectConnection d13 =
                new DirectConnection(vegas, SLO, 4, ColorTrains.GREEN);
        DirectConnection d14 =
                new DirectConnection(vegas, SLO, 4, ColorTrains.WHITE);
        DirectConnection d15 =
                new DirectConnection(vegas, SLO, 5, ColorTrains.BLUE);
        DirectConnection d16 =
                new DirectConnection(vegas, SLO, 5, ColorTrains.RED);
        DirectConnection d17 =
                new DirectConnection(vegas, SLO, 5, ColorTrains.GREEN);
        DirectConnection d18 =
                new DirectConnection(vegas, SLO, 5, ColorTrains.WHITE);

        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        connections.add(d6);
        connections.add(d7);
        connections.add(d8);
        connections.add(d9);
        connections.add(d10);
        connections.add(d11);
        connections.add(d12);
        connections.add(d13);
        connections.add(d14);
        connections.add(d15);
        connections.add(d16);
        connections.add(d17);
        connections.add(d18);

        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    public static TrainsMap createSquareMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.9f, .1f));
        City SAC = new City("Sac", new Coord(.1f, .9f));
        City SLO = new City("SLO", new Coord(.9f, .9f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        DirectConnection d1 = new DirectConnection(LA, SLO, 3, ColorTrains.RED);
        DirectConnection d2 =
                new DirectConnection(SLO, SAC, 5, ColorTrains.GREEN);
        DirectConnection d3 =
                new DirectConnection(SLO, SF, 5, ColorTrains.WHITE);
        DirectConnection d4 =
                new DirectConnection(SF, SAC, 4, ColorTrains.BLUE);
        DirectConnection d5 = new DirectConnection(LA, SF, 4, ColorTrains.RED);
        DirectConnection d6 =
                new DirectConnection(SAC, LA, 4, ColorTrains.WHITE);
        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        connections.add(d6);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    public static TrainsMap createAlphabeticMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City A = new City("A", new Coord(.1f, .1f));
        City B = new City("B", new Coord(.9f, .1f));
        City C = new City("C", new Coord(.1f, .9f));
        City D = new City("D", new Coord(.9f, .9f));
        cities.add(A);
        cities.add(B);
        cities.add(C);
        cities.add(D);
        DirectConnection d1 = new DirectConnection(A, D, 3, ColorTrains.RED);
        DirectConnection d2 = new DirectConnection(D, C, 5, ColorTrains.GREEN);
        DirectConnection d3 = new DirectConnection(D, B, 5, ColorTrains.WHITE);
        DirectConnection d4 = new DirectConnection(B, C, 4, ColorTrains.BLUE);
        DirectConnection d5 = new DirectConnection(A, B, 4, ColorTrains.RED);
        DirectConnection d6 = new DirectConnection(C, A, 4, ColorTrains.WHITE);
        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        connections.add(d6);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    public static TrainsMap createComplicatedMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City brookline = new City("Brookline", new Coord(.1f, .2f));
        City common = new City("Common", new Coord(.5f, .3f));
        City chinatown = new City("Chinatown", new Coord(.6f, .4f));
        City financial_district =
                new City("Financial District  East", new Coord(.80f, .20f));
        City seaport = new City("Seaport", new Coord(.90f, .45f));
        City cambridge = new City("cambridge", new Coord(.11f, .101f));
        City maine = new City("Maine", new Coord(1f, 0f));
        City washington = new City("Washington", new Coord(0f, 0f));
        City sd = new City("San Diego", new Coord(0f, 1f));
        City florida = new City("Florida", new Coord(1f, 1f));

        cities.add(common);
        cities.add(chinatown);
        cities.add(financial_district);
        cities.add(seaport);
        cities.add(brookline);
        cities.add(cambridge);
        cities.add(maine);
        cities.add(washington);
        cities.add(sd);
        cities.add(florida);

        DirectConnection d8 =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection d10 =
                new DirectConnection(brookline, common, 3, ColorTrains.RED);
        DirectConnection d11 =
                new DirectConnection(brookline, common, 4, ColorTrains.BLUE);
        DirectConnection d2 =
                new DirectConnection(brookline, common, 3, ColorTrains.GREEN);

        DirectConnection d1 =
                new DirectConnection(cambridge, common, 3, ColorTrains.RED);
        DirectConnection d5 =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection d9 =
                new DirectConnection(cambridge, maine, 5, ColorTrains.GREEN);
        DirectConnection d12 =
                new DirectConnection(cambridge, financial_district, 4,
                        ColorTrains.BLUE);

        DirectConnection d3 =
                new DirectConnection(common, chinatown, 5, ColorTrains.WHITE);
        DirectConnection d6 =
                new DirectConnection(common, financial_district, 4,
                        ColorTrains.WHITE);
        DirectConnection d4 =
                new DirectConnection(common, seaport, 4, ColorTrains.BLUE);

        DirectConnection d7 =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);

        DirectConnection d13 =
                new DirectConnection(washington, sd, 5, ColorTrains.BLUE);
        DirectConnection d14 =
                new DirectConnection(sd, florida, 5, ColorTrains.WHITE);
        DirectConnection d15 =
                new DirectConnection(florida, maine, 5, ColorTrains.BLUE);
        DirectConnection d16 =
                new DirectConnection(maine, washington, 5, ColorTrains.WHITE);

        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        connections.add(d6);
        connections.add(d7);
        connections.add(d8);
        connections.add(d9);
        connections.add(d10);
        connections.add(d11);
        connections.add(d12);

        connections.add(d13);
        connections.add(d14);
        connections.add(d15);
        connections.add(d16);

//        TrainsMap map =  TrainsMap.createTrainsMapWithDefaultSize(cities,
//        connections);
        TrainsMap map = new TrainsMap(cities, connections, 600, 600);
        return map;
    }

    public static TrainsMap createBostonMap() {
        Set<City> cities = new HashSet<City>();
        Set<DirectConnection> connections = new HashSet<DirectConnection>();
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));

        cities.add(common);
        cities.add(chinatown);
        cities.add(financial_district);
        cities.add(seaport);
        cities.add(brookline);
        cities.add(cambridge);

        DirectConnection d8 =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection d11 =
                new DirectConnection(brookline, common, 4, ColorTrains.BLUE);
        DirectConnection d2 =
                new DirectConnection(brookline, common, 3, ColorTrains.GREEN);

        DirectConnection d1 =
                new DirectConnection(cambridge, common, 3, ColorTrains.RED);
        DirectConnection d5 =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection d12 =
                new DirectConnection(cambridge, financial_district, 4,
                        ColorTrains.BLUE);

        DirectConnection d3 =
                new DirectConnection(common, chinatown, 3, ColorTrains.WHITE);
        DirectConnection d6 =
                new DirectConnection(common, financial_district, 4,
                        ColorTrains.WHITE);
        DirectConnection d4 =
                new DirectConnection(common, seaport, 4, ColorTrains.BLUE);

        DirectConnection d7 =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);

        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        connections.add(d6);
        connections.add(d7);
        connections.add(d8);
        connections.add(d11);
        connections.add(d12);

        TrainsMap map = new TrainsMap(cities, connections, 800, 800);
        return map;
    }

    public static TrainsMap createCaliforniaMap() {
        Set<City> cities = new HashSet<City>();
        Set<DirectConnection> connections = new HashSet<DirectConnection>();
        City arcadia = new City("Arcadia", new Coord(.1f, .5f));
        City bakersfield = new City("Bakersfield", new Coord(.5f, .4f));
        City cupertino = new City("Cupertino", new Coord(.68f, .42f));
        City irvine = new City("Irvine", new Coord(.80f, .3f));
        City santa = new City("Santa Barbara", new Coord(.90f, .6f));
        City tahoe = new City("Tahoe", new Coord(.11f, .101f));
        City maine = new City("Maine", new Coord(.9f, .05f));
        City florida = new City("Florida", new Coord(.97f, .9f));
        City tijuana = new City("Tijuana", new Coord(.05f, .9f));

        cities.add(bakersfield);
        cities.add(cupertino);
        cities.add(irvine);
        cities.add(santa);
        cities.add(arcadia);
        cities.add(tahoe);
        cities.add(maine);
        cities.add(florida);
        cities.add(tijuana);

        DirectConnection d21 =
                new DirectConnection(maine, tahoe, 5, ColorTrains.BLUE);

        DirectConnection d17 =
                new DirectConnection(maine, florida, 5, ColorTrains.BLUE);
        DirectConnection d18 =
                new DirectConnection(florida, tijuana, 5, ColorTrains.WHITE);

        DirectConnection d19 =
                new DirectConnection(tijuana, arcadia, 5, ColorTrains.GREEN);
        DirectConnection d20 =
                new DirectConnection(maine, florida, 5, ColorTrains.WHITE);

        DirectConnection d8 =
                new DirectConnection(arcadia, tahoe, 5, ColorTrains.RED);
        DirectConnection d11 =
                new DirectConnection(arcadia, bakersfield, 4, ColorTrains.BLUE);
        DirectConnection d2 = new DirectConnection(arcadia, bakersfield, 3,
                ColorTrains.GREEN);
        DirectConnection d13 = new DirectConnection(arcadia, bakersfield, 5,
                ColorTrains.GREEN);

        DirectConnection d1 =
                new DirectConnection(tahoe, bakersfield, 3, ColorTrains.RED);
        DirectConnection d14 =
                new DirectConnection(tahoe, bakersfield, 5, ColorTrains.BLUE);

        DirectConnection d5 =
                new DirectConnection(tahoe, irvine, 5, ColorTrains.RED);
        DirectConnection d12 =
                new DirectConnection(tahoe, irvine, 4, ColorTrains.BLUE);

        DirectConnection d3 = new DirectConnection(bakersfield, cupertino, 3,
                ColorTrains.WHITE);
        DirectConnection d6 =
                new DirectConnection(bakersfield, irvine, 4, ColorTrains.WHITE);
        DirectConnection d4 =
                new DirectConnection(bakersfield, santa, 4, ColorTrains.BLUE);
        DirectConnection d15 =
                new DirectConnection(bakersfield, santa, 5, ColorTrains.BLUE);

        DirectConnection d7 =
                new DirectConnection(irvine, santa, 4, ColorTrains.WHITE);
        DirectConnection d16 =
                new DirectConnection(irvine, santa, 4, ColorTrains.RED);

        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        connections.add(d6);
        connections.add(d7);
        connections.add(d8);
        connections.add(d11);
        connections.add(d12);
        connections.add(d13);
        connections.add(d14);
        connections.add(d15);
        connections.add(d16);
        connections.add(d17);
        connections.add(d18);
        connections.add(d19);
        connections.add(d20);
        connections.add(d21);

        TrainsMap map = new TrainsMap(cities, connections, 800, 800);
        return map;
    }

    public static TrainsMap createBigBostonMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City maine = new City("Maine", new Coord(.92f, .08f));
        City washington = new City("Washington", new Coord(.02f, .02f));
        City florida = new City("Florida", new Coord(.98f, .98f));
        City california = new City("California", new Coord(.08f, .85f));

        cities.add(common);
        cities.add(chinatown);
        cities.add(financial_district);
        cities.add(seaport);
        cities.add(brookline);
        cities.add(cambridge);
        cities.add(maine);
        cities.add(washington);
        cities.add(florida);
        cities.add(california);

        DirectConnection d8 =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection d11 =
                new DirectConnection(brookline, common, 4, ColorTrains.BLUE);
        DirectConnection d2 =
                new DirectConnection(brookline, common, 3, ColorTrains.GREEN);

        DirectConnection d1 =
                new DirectConnection(cambridge, common, 3, ColorTrains.RED);
        DirectConnection d5 =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection d12 =
                new DirectConnection(cambridge, financial_district, 4,
                        ColorTrains.BLUE);

        DirectConnection d3 =
                new DirectConnection(common, chinatown, 3, ColorTrains.WHITE);
        DirectConnection d6 =
                new DirectConnection(common, financial_district, 4,
                        ColorTrains.WHITE);
        DirectConnection d4 =
                new DirectConnection(common, seaport, 4, ColorTrains.BLUE);

        DirectConnection d7 =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);
        DirectConnection d13 = new DirectConnection(cambridge, washington, 5,
                ColorTrains.WHITE);
        DirectConnection d14 = new DirectConnection(cambridge, washington, 5,
                ColorTrains.BLUE);
        DirectConnection d15 =
                new DirectConnection(seaport, maine, 5, ColorTrains.BLUE);
        DirectConnection d16 =
                new DirectConnection(florida, maine, 5, ColorTrains.GREEN);
        DirectConnection d17 =
                new DirectConnection(washington, maine, 5, ColorTrains.BLUE);
        DirectConnection d18 =
                new DirectConnection(cambridge, maine, 5, ColorTrains.RED);
        DirectConnection d20 =
                new DirectConnection(cambridge, maine, 5, ColorTrains.BLUE);
        DirectConnection d21 =
                new DirectConnection(cambridge, maine, 5, ColorTrains.WHITE);
        DirectConnection d22 = new DirectConnection(washington, california, 5,
                ColorTrains.BLUE);
        DirectConnection d23 =
                new DirectConnection(florida, california, 5, ColorTrains.RED);
        DirectConnection d24 =
                new DirectConnection(florida, california, 5, ColorTrains.WHITE);
        DirectConnection d25 = new DirectConnection(brookline, california, 5,
                ColorTrains.WHITE);

        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        connections.add(d6);
        connections.add(d7);
        connections.add(d8);
        connections.add(d11);
        connections.add(d12);
        connections.add(d13);
        connections.add(d14);
        connections.add(d15);
        connections.add(d16);
        connections.add(d17);
        connections.add(d18);
        connections.add(d20);
        connections.add(d21);
        connections.add(d22);
        connections.add(d23);
        connections.add(d24);
        connections.add(d25);

        TrainsMap map = new TrainsMap(cities, connections, 800, 800);
        return map;
    }

    public static TrainsMap createEdgeCitiesMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City brookline =
                new City("BrooklineHarvardBrookline", new Coord(.0f, .0f));
        City common = new City("Common", new Coord(.5f, .3f));
        City chinatown = new City("Chinatown", new Coord(.6f, .4f));
        City financial_district =
                new City("Financial District", new Coord(1f, .20f));
        City seaport = new City("seaport", new Coord(1f, .45f));
        City cambridge = new City("cambridgecambridge", new Coord(.12f, .12f));

        cities.add(common);
        cities.add(chinatown);
        cities.add(financial_district);
        cities.add(seaport);
        cities.add(brookline);
        cities.add(cambridge);
        DirectConnection d1 =
                new DirectConnection(cambridge, common, 3, ColorTrains.RED);
        DirectConnection d2 =
                new DirectConnection(brookline, common, 3, ColorTrains.GREEN);
        DirectConnection d3 =
                new DirectConnection(common, chinatown, 5, ColorTrains.WHITE);
        DirectConnection d4 =
                new DirectConnection(common, seaport, 4, ColorTrains.BLUE);
        DirectConnection d5 =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection d6 =
                new DirectConnection(common, financial_district, 4,
                        ColorTrains.WHITE);
        DirectConnection d7 =
                new DirectConnection(seaport, financial_district, 4,
                        ColorTrains.WHITE);
        DirectConnection d8 =
                new DirectConnection(common, chinatown, 3, ColorTrains.GREEN);

        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        connections.add(d6);
        connections.add(d7);
        connections.add(d8);

        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    public static TrainsMap createTinyMap() {
        Set<City> cities = new HashSet<City>(2);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(1);
        City Alb = new City("Albany", new Coord(.123445678f, .33445433f));
        City Del = new City("Delmar", new Coord(.53423425f, .824242342f));
        cities.add(Alb);
        cities.add(Del);
        DirectConnection connect =
                new DirectConnection(Alb, Del, 5, ColorTrains.BLUE);
        connections.add(connect);
        TrainsMap map = new TrainsMap(cities, connections, 10, 800);
        return map;
    }
}
