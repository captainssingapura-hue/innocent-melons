package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.buildings.Standing;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.bay.land.MarinaBay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The bay's refined towers, one list: each by its name, where it stands in the bay, its model. Whatever shows or plays
 * the bay's towers takes them from here, in this order; each drawn by its own keys, given in the same order from one
 * first key, so no two share any. A tower more is a line more here.
 *
 * <p>Where each stands ({@link MarinaBay}'s frame, cm): its middle where the map has it, turned as its sides run
 * there, its feet on the bay's land.
 * <ul>
 * <li>The financial centre's three office towers ({@link Mbfc}), their plans' north along: which way each faces is the
 * map's, and agrees with each one's outline, Tower 1's notch to its north-west, Tower 2's long sides across, Tower 3's
 * step at its south-west.
 * <li>Its two homes towers ({@link MbfcHomes}) and The Sail's two ({@link Sail}): on the map's outlines, turned as
 * those are made their own ({@link Mapped}).
 * <li>One Raffles Quay's two ({@link Orq}): the North Tower's plan's north to its north-west face, where the map draws
 * the jogs its plan has at that face's corners; the South Tower's long sides along the longer pair of the map's, its
 * outline drawn small there.
 * <li>Asia Square's two ({@link AsiaSquare}): the map draws both the same, 64.6 by 56.5 m, its long sides at 106°;
 * Tower 1's plan's long sides along them, Tower 2's, nearly square, its longer sides along them. Which end of each is
 * its plan's top, the plans do not say: a guess.
 * <li>The business district's towers round Raffles Place, Collyer Quay, Shenton Way and Tanjong Pagar, and Marina
 * One's: on the map's outlines, as the homes towers are; One Raffles Place's Tower 2 its plans' plate set at the middle
 * of the map's, turned as it is.
 * <li>The rest of the district's towers of 150 m and up, offices, homes and hotels, round Raffles Place, Shenton Way,
 * Anson Road, Tanjong Pagar, Marina Centre and City Hall: on the map's outlines, simplified to within half a metre.
 * </ul>
 */
public final class Refined {

    private Refined() {
    }

    /** The first key the bay's towers are drawn by, each taking {@link Standing#KEYS} from it in turn. */
    public static final int KEYS = 975_000;

    /**
     * One of the bay's refined towers.
     *
     * @param name  what it is called, as its model calls it
     * @param x     where its middle stands in the bay, across, cm
     * @param z     where its middle stands in the bay, along, cm
     * @param yaw   which way it is turned there, degrees
     * @param model its model, made when asked for
     */
    public record Tower(String name, double x, double z, double yaw, Supplier<OfficeTower> model) {
    }

    /** The bay's refined towers, in order. */
    public static final List<Tower> TOWERS = List.of(
            new Tower("MBFC Tower 1", -58_590, -57_440, 3.7, Mbfc::tower1),
            new Tower("MBFC Tower 2", -60_880, -64_900, 16.1, Mbfc::tower2),
            new Tower("MBFC Tower 3", -52_350, -69_450, -8.6, Mbfc::tower3),
            mapped("Marina Bay Residences", MbfcHomes.RESIDENCES, MbfcHomes::residences),
            mapped("Marina Bay Suites", MbfcHomes.SUITES, MbfcHomes::suites),
            new Tower("ORQ North Tower", -85_040, -49_850, -48.3, Orq::north),
            new Tower("ORQ South Tower", -87_950, -57_550, -74.3, Orq::south),
            mapped("The Sail, Marina Bay Tower", Sail.MARINA_BAY_TOWER, Sail::marinaBayTower),
            mapped("The Sail, Central Park Tower", Sail.CENTRAL_PARK_TOWER, Sail::centralParkTower),
            new Tower("Asia Square Tower 1", -85_590, -78_710, -74.2, AsiaSquare::tower1),
            new Tower("Asia Square Tower 2", -88_680, -89_810, 15.9, AsiaSquare::tower2),
            mapped("IOI Central Boulevard West Tower", IoiCentralBoulevard.WEST_TOWER, IoiCentralBoulevard::westTower),
            mapped("Marina One East Tower", MarinaOne.EAST_TOWER, MarinaOne::eastTower),
            mapped("Marina One West Tower", MarinaOne.WEST_TOWER, MarinaOne::westTower),
            mapped("Ocean Financial Centre", OceanFinancialCentre.MAPPED, OceanFinancialCentre::tower),
            mapped("One Raffles Place Tower 1", OneRafflesPlace.TOWER_1, OneRafflesPlace::tower1),
            mapped("One Raffles Place Tower 2", OneRafflesPlace.TOWER_2, OneRafflesPlace::tower2),
            mapped("Republic Plaza", RepublicPlaza.MAPPED, RepublicPlaza::tower),
            mapped("UOB Plaza One", UobPlaza.MAPPED, UobPlaza::tower),
            mapped("CapitaGreen", CapitaGreen.MAPPED, CapitaGreen::tower),
            mapped("One Shenton Tower 1", OneShenton.TOWER_1, OneShenton::tower1),
            mapped("One Shenton Tower 2", OneShenton.TOWER_2, OneShenton::tower2),
            mapped("Capital Tower", CapitalTower.MAPPED, CapitalTower::tower),
            mapped("Guoco Tower", GuocoTower.MAPPED, GuocoTower::tower),
mapped("Singapore Land Tower", SingaporeLandTower.MAPPED, SingaporeLandTower::tower),
            mapped("OCBC Centre", OcbcCentre.MAPPED, OcbcCentre::tower),
            mapped("Six Battery Road", SixBatteryRoad.MAPPED, SixBatteryRoad::tower),
            mapped("Maybank Tower", MaybankTower.MAPPED, MaybankTower::tower),
            mapped("Bank of China Building", BankOfChinaBuilding.MAPPED, BankOfChinaBuilding::tower),
            mapped("Chevron House", ChevronHouse.MAPPED, ChevronHouse::tower),
            mapped("Hitachi Tower", HitachiTower.MAPPED, HitachiTower::tower),
            mapped("UOB Plaza Two", UobPlazaTwo.MAPPED, UobPlazaTwo::tower),
            mapped("Samsung Hub", SamsungHub.MAPPED, SamsungHub::tower),
            mapped("NTUC Centre", NtucCentre.MAPPED, NtucCentre::tower),
            mapped("Hong Leong Building", HongLeongBuilding.MAPPED, HongLeongBuilding::tower),
            mapped("One George Street", OneGeorgeStreet.MAPPED, OneGeorgeStreet::tower),
            mapped("Oxley Tower", OxleyTower.MAPPED, OxleyTower::tower),
            mapped("18 Robinson", EighteenRobinson.MAPPED, EighteenRobinson::tower),
            mapped("Frasers Tower", FrasersTower.MAPPED, FrasersTower::tower),
            mapped("Keppel South Central", KeppelSouthCentral.MAPPED, KeppelSouthCentral::tower),
            mapped("OUE Downtown Tower 1", OueDowntown.TOWER1, OueDowntown::tower1),
            mapped("OUE Downtown Tower 2", OueDowntown.TOWER2, OueDowntown::tower2),
            mapped("Springleaf Tower", SpringleafTower.MAPPED, SpringleafTower::tower),
            mapped("Lippo Centre", LippoCentre.MAPPED, LippoCentre::tower),
            mapped("SBF Center", SbfCenter.MAPPED, SbfCenter::tower),
            mapped("Robinson 77", RobinsonSeventySeven.MAPPED, RobinsonSeventySeven::tower),
            mapped("SGX Centre 1", SgxCentre.CENTRE1, SgxCentre::centre1),
            mapped("SGX Centre 2", SgxCentre.CENTRE2, SgxCentre::centre2),
            mapped("Sky Suites@Anson", SkySuitesAnson.MAPPED, SkySuitesAnson::tower),
            mapped("Altez", Altez.MAPPED, Altez::tower),
            mapped("V on Shenton", VOnShenton.MAPPED, VOnShenton::tower),
            mapped("Lumiere", Lumiere.MAPPED, Lumiere::tower),
            mapped("Icon Loft Tower 1", IconLoft.TOWER1, IconLoft::tower1),
            mapped("Icon Loft Tower 2", IconLoft.TOWER2, IconLoft::tower2),
            mapped("The Pinnacle@Duxton Block 1A", PinnacleDuxton.BLOCK_A, PinnacleDuxton::blockA),
            mapped("The Pinnacle@Duxton Block 1B", PinnacleDuxton.BLOCK_B, PinnacleDuxton::blockB),
            mapped("The Pinnacle@Duxton Block 1C", PinnacleDuxton.BLOCK_C, PinnacleDuxton::blockC),
            mapped("The Pinnacle@Duxton Block 1D", PinnacleDuxton.BLOCK_D, PinnacleDuxton::blockD),
            mapped("The Pinnacle@Duxton Block 1E", PinnacleDuxton.BLOCK_E, PinnacleDuxton::blockE),
            mapped("The Pinnacle@Duxton Block 1F", PinnacleDuxton.BLOCK_F, PinnacleDuxton::blockF),
            mapped("The Pinnacle@Duxton Block 1G", PinnacleDuxton.BLOCK_G, PinnacleDuxton::blockG),
            mapped("One Pearl Bank, west tower", OnePearlBank.WEST_TOWER, OnePearlBank::westTower),
            mapped("One Pearl Bank, east tower", OnePearlBank.EAST_TOWER, OnePearlBank::eastTower),
            mapped("International Plaza", InternationalPlaza.MAPPED, InternationalPlaza::tower),
            mapped("Millenia Tower", MilleniaTower.MAPPED, MilleniaTower::tower),
            mapped("Centennial Tower", CentennialTower.MAPPED, CentennialTower::tower),
            mapped("Suntec Tower 1", SuntecCity.TOWER1, SuntecCity::tower1),
            mapped("Suntec Tower 2", SuntecCity.TOWER2, SuntecCity::tower2),
            mapped("Suntec Tower 3", SuntecCity.TOWER3, SuntecCity::tower3),
            mapped("Suntec Tower 4", SuntecCity.TOWER4, SuntecCity::tower4),
            mapped("DUO Tower", Duo.TOWER, Duo::tower),
            mapped("DUO Residences", Duo.RESIDENCES, Duo::residences),
            mapped("Swissôtel The Stamford", SwissotelStamford.MAPPED, SwissotelStamford::tower),
            mapped("Raffles City Tower", RafflesCityTower.MAPPED, RafflesCityTower::tower),
            mapped("Gateway East", Gateway.EAST, Gateway::east),
            mapped("Gateway West", Gateway.WEST, Gateway::west),
            mapped("State Courts Office Tower", StateCourts.OFFICE_TOWER, StateCourts::officeTower),
            mapped("State Courts Court Tower", StateCourts.COURT_TOWER, StateCourts::courtTower),
            mapped("Pickering Operations Complex", PickeringOperationsComplex.MAPPED, PickeringOperationsComplex::tower),
            mapped("Oasia Hotel Downtown", OasiaHotelDowntown.MAPPED, OasiaHotelDowntown::tower),
            mapped("CanningHill Piers", CanningHillPiers.MAPPED, CanningHillPiers::tower));

    private static Tower mapped(final String name, final Mapped mapped, final Supplier<OfficeTower> model) {
        return new Tower(name, mapped.x(), mapped.z(), mapped.yaw(), model);
    }

    /** Each where it stands in the bay, its feet on its land, drawn by its keys: each made now. */
    public static List<Standing> standing() {
        final Land land = MarinaBay.land();
        final List<Standing> standing = new ArrayList<>();
        for (int k = 0; k < TOWERS.size(); k++) {
            final Tower t = TOWERS.get(k);
            standing.add(new Standing(t.model().get(), new Place(t.x(), t.z(), land.height(t.x(), t.z()), t.yaw()),
                    KEYS + k * Standing.KEYS));
        }
        return standing;
    }
}
