package tp.antonius.lifeemulator.service;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import tp.antonius.lifeemulator.service.coder.DataCoder;
import tp.antonius.lifeemulator.view.CellInfoPanel;
import tp.antonius.lifeemulator.view.EventsInfoPanel;
import tp.antonius.lifeemulator.view.MainPanel;
import tp.antonius.lifeemulator.view.render.MapPanel;

public class HumanAction {
	
	private static int date = 1;
	private static Timer timer;
	private static int timerDelay = 100;
	
	public static void start() {
		if (timer == null) {
			timer = new Timer(timerDelay, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MapPanel map = MainPanel.mapPanel;
					for (int y = 0; y < MapPanel.MAP_SIZE; y++) {
						for (int x = 0; x < MapPanel.MAP_SIZE; x++) {
							map.setActiveFlagHumanAt(1, y, x);
							map.setActiveFlagPlantAt(1, y, x);
						}
					}
					for (int y = 0; y < MapPanel.MAP_SIZE; y++) {
						for (int x = 0; x < MapPanel.MAP_SIZE; x++) {
							act(map.getRawDataAt(y, x), y, x);
						}
					}
					map.repaint();					
					int y = map.getSelectedRow();
					int x = map.getSelectedColumn();
					CellInfoPanel cellInfo = MainPanel.cellInfoPanel;
					if (y != -1 && x != -1) {
						cellInfo.update(y, x);
					}
					else {
						cellInfo.reset();
					}
					StatisticCounter.update();
					MainPanel.mapInfoPanel.update(++date);
				}
			});
		}
		timer.start();
	}
	
	public static void pause() {
		if (timer != null) {
			timer.stop();	
		}
	}
	
	public static void stop() {
		if (timer != null) {
			timer.stop();	
		}
		date = 1;
	}
	
	// ACT
	private static void act(long cellData, int y, int x) {
		if (DataCoder.decodeActiveFlagHuman(cellData) == 1 && DataCoder.decodeHumanType(cellData) != DataCoder.HUMAN_TYPE_EMPTY) {
			if (tryToDie(cellData, y, x) 
					|| tryToGiveBirth(cellData, y, x)
					|| tryToSleep(cellData, y, x) 
					|| tryToEat(cellData, y, x)
					|| tryToMakeChild(cellData, y, x)
					|| tryToMove(cellData, y, x, 0, 0)) {/*Do nothing*/}
		}
		if (DataCoder.decodeActiveFlagPlant(cellData) == 1 && DataCoder.decodePlantType(cellData) != DataCoder.PLANT_TYPE_EMPTY) {
			tryToMakeFruits(cellData, y, x);
			tryToDropFruit(cellData, y, x);
		}
	}
	
	// HUMAN - DIE
	private static boolean tryToDie(long cellData, int y, int x) {
		EventsInfoPanel events = MainPanel.eventsInfoPanel;
		int energy = DataCoder.decodeHumanEnergy(cellData);
		if (energy == 0) {
			clearHuman(y, x);
			StatisticCounter.peopleDied++;
			StatisticCounter.peopleDiedByEnergy++;
			events.update(date, "Human died by [Low Energy].");
			return true;
		}
		int satiety = DataCoder.decodeHumanSatiety(cellData);
		if (satiety == 0) {
			clearHuman(y, x);
			StatisticCounter.peopleDied++;
			StatisticCounter.peopleDiedBySatiety++;
			events.update(date, "Human died [Low Satiety].");
			return true;
		}
		// f(x): f(0..9000) <= 0, f(24000..32767) >= 100;
		// f(x) = 2*(x/300) - 60;
		// Every year + 2% (since 30 years)
		boolean decision = ParamGenerator.generateBoolean(2 * (DataCoder.decodeHumanAge(cellData) / 300) - 60);
		if (decision) {
			clearHuman(y, x);
			StatisticCounter.peopleDied++;
			StatisticCounter.peopleDiedByAge++;
			events.update(date, "Human died [Age].");
			return true;
		}
		return false;
	}
	
	// HUMAN - EAT
	private static boolean tryToEat(long cellData, int y, int x) {
		MapPanel map = MainPanel.mapPanel;
		// f(x): f(0..10) >= 100, f(60..64) <= 0;
		// f(x) = 120 - 2*x;
		boolean decision = ParamGenerator.generateBoolean(120 - 2 * DataCoder.decodeHumanSatiety(cellData));
		if (decision) {
			for (int yShift = -1; yShift < 2; yShift++) {
				for (int xShift = -1; xShift < 2; xShift++) {
					int yTarget = y + yShift;
					int xTarget = x + xShift;
					if (isCellInMapRange(yTarget, xTarget)) {
						int fruitsTarget = map.getPlantFruitsAt(yTarget, xTarget);
						if (fruitsTarget != 0) {
							map.setPlantFruitsAt(--fruitsTarget, yTarget, xTarget);
							if (map.getHumanAgeAt(y, x) < 10) {
								map.setHumanSatietyAt(map.getHumanSatietyAt(y, x) + 32, y, x);
							}
							else {
								map.setHumanSatietyAt(map.getHumanSatietyAt(y, x) + 16, y, x);
							}
							map.setHumanEnergyAt(map.getHumanEnergyAt(y, x) - 1, y, x);
							map.setHumanAgeAt(map.getHumanAgeAt(y, x) + 1, y, x);
							map.setActiveFlagHumanAt(0, y, x);							
							return true;
						}
					}
				}	
			}
			int minTarget = MapPanel.MAP_SIZE * MapPanel.MAP_SIZE + MapPanel.MAP_SIZE * MapPanel.MAP_SIZE;
			int yTarget = y;
			int xTarget = x;
			for (int yTemp = 0; yTemp < MapPanel.MAP_SIZE; yTemp++) {
				for (int xTemp = 0; xTemp < MapPanel.MAP_SIZE; xTemp++) {					
					if (map.getPlantFruitsAt(yTemp, xTemp) != 0) {
						int yDelta = Math.abs(yTemp - y);
						int xDelta = Math.abs(xTemp - x);
						int minTemp = yDelta * yDelta + xDelta * xDelta;
						if (minTemp < minTarget) {
							minTarget = minTemp;
							yTarget = yTemp;
							xTarget = xTemp;
						}
					}
				}
			}
			if (yTarget != y || xTarget != x) {
				int yShift = 0;
				int xShift = 0;
				int yDelta = yTarget - y;
				int xDelta = xTarget - x;
				if (yDelta < 0) {
					yShift = -1;
				}
				else if (yDelta > 0) {
					yShift = 1;
				}
				if (xDelta < 0) {
					xShift = -1;
				}
				else if (xDelta > 0) {
					xShift = 1;
				}
				return tryToMove(cellData, y, x, yShift, xShift);
			}
		}
		return false;
	}
	
	// HUMAN - SLEEP
	private static boolean tryToSleep(long cellData, int y, int x) {
		// f(x): f(0..10) >= 100, f(60..64) <= 0;
		// f(x) = 120 - 2*x;
		boolean decision = ParamGenerator.generateBoolean(120 - 2 * DataCoder.decodeHumanEnergy(cellData));
		if (decision) {
			MapPanel map = MainPanel.mapPanel;
			map.setHumanEnergyAt(63, y, x);
			map.setHumanSatietyAt(map.getHumanSatietyAt(y, x) - 1, y, x);
			map.setHumanAgeAt(map.getHumanAgeAt(y, x) + 1, y, x);
			map.setActiveFlagHumanAt(0, y, x);
			return true;
		}
		return false;
	}
	
	// HUMAN - MAKE CHILD
	private static boolean tryToMakeChild(long cellData, int y, int x) {
		MapPanel map = MainPanel.mapPanel;
		if (map.getHumanTypeAt(y, x) == DataCoder.HUMAN_TYPE_WOMAN && map.getHumanPregnancyAt(y, x) == 0) {
			for (int yShift = -1; yShift < 2; yShift++) {
				for (int xShift = -1; xShift < 2; xShift++) {
					int yTarget = y + yShift;
					int xTarget = x + xShift;
					if (isCellInMapRange(yTarget, xTarget)
							&& map.getHumanTypeAt(yTarget, xTarget) == DataCoder.HUMAN_TYPE_MAN 
							&& map.getActiveFlagHumanAt(yTarget, xTarget) == 1) {
						boolean decision = ParamGenerator.generateBoolean(30);
						if (decision) {
							map.setHumanPregnancyAt(1, y, x);
							map.setHumanEnergyAt(map.getHumanEnergyAt(y, x) - 4, y, x);
							map.setHumanSatietyAt(map.getHumanSatietyAt(y, x) - 4, y, x);
							map.setHumanAgeAt(map.getHumanAgeAt(y, x) + 1, y, x);
							map.setActiveFlagHumanAt(0, y, x);
							map.setHumanEnergyAt(map.getHumanEnergyAt(yTarget, xTarget) - 4, yTarget, xTarget);
							map.setHumanSatietyAt(map.getHumanSatietyAt(yTarget, xTarget) - 4, yTarget, xTarget);
							map.setHumanAgeAt(map.getHumanAgeAt(yTarget, xTarget) + 1, yTarget, xTarget);
							map.setActiveFlagHumanAt(0, yTarget, xTarget);
							MainPanel.eventsInfoPanel.update(date, "Woman got pregnant.");
							return true;
						}
					}
				}	
			}
		}
		return false;
	}
	
	// HUMAN - GIVE BIRTH
	private static boolean tryToGiveBirth(long cellData, int y, int x) {
		MapPanel map = MainPanel.mapPanel;
		int pregnacy = map.getHumanPregnancyAt(y, x);
		if (pregnacy != 0 && pregnacy < 300) {
			map.setHumanPregnancyAt(map.getHumanPregnancyAt(y, x) + 1, y, x);
		}
		else if (pregnacy == 300) {
			map.setHumanPregnancyAt(0, y, x);
			map.setHumanEnergyAt(map.getHumanEnergyAt(y, x) - 4, y, x);
			map.setHumanSatietyAt(map.getHumanSatietyAt(y, x) - 4, y, x);
			map.setHumanAgeAt(map.getHumanAgeAt(y, x) + 1, y, x);
			map.setActiveFlagHumanAt(0, y, x);
			for (int yShift = -1; yShift < 2; yShift++) {
				for (int xShift = -1; xShift < 2; xShift++) {
					int yTarget = y + yShift;
					int xTarget = x + xShift;
					if (isCellInMapRange(yTarget, xTarget) && map.getHumanTypeAt(yTarget, xTarget) == DataCoder.HUMAN_TYPE_EMPTY) {
						map.setHumanTypeAt(ParamGenerator.generateBoolean() ? DataCoder.HUMAN_TYPE_MAN : DataCoder.HUMAN_TYPE_WOMAN, yTarget, xTarget);
						map.setHumanAgeAt(301, yTarget, xTarget);
						map.setHumanEnergyAt(63, yTarget, xTarget);
						map.setHumanSatietyAt(63, yTarget, xTarget);
						map.setHumanPregnancyAt(0, yTarget, xTarget);
						map.setActiveFlagHumanAt(0, yTarget, xTarget);
						StatisticCounter.childrenWereBorn++;
						MainPanel.eventsInfoPanel.update(date, "Child was born.");
						return true;
					}
				}
			}
			map.setHumanPregnancyAt(0, y, x);
			StatisticCounter.childrenDied++;
			MainPanel.eventsInfoPanel.update(date, "Child died.");
			return true;
		}
		return false;
	}
	
	// HUMAN - MOVE
	private static boolean tryToMove(long cellData, int y, int x, int yShift, int xShift) {
		MapPanel map = MainPanel.mapPanel;
		if (yShift == 0 && xShift == 0) {
			yShift = ParamGenerator.generateInteger(-1, 1);	
			xShift = ParamGenerator.generateInteger(-1, 1);
		}
		if (yShift != 0 || xShift != 0) {
			int yTarget = y + yShift;
			int xTarget = x + xShift;
			if (!isCellInMapRange(yTarget, xTarget)) {
				clearHuman(y, x);
				StatisticCounter.peopleDied++;
				StatisticCounter.peopleDiedByLost++;
				MainPanel.eventsInfoPanel.update(date, "Human died [Lost].");
				return true;
			}
			if (map.getHumanTypeAt(yTarget, xTarget) == 0) {
				moveHuman(y, x, yTarget, xTarget);
				int landscapeTarget = map.getLandscapeTypeAt(yTarget, xTarget);
				if (landscapeTarget == DataCoder.LANDSCAPE_TYPE_WATER_LOW || landscapeTarget == DataCoder.LANDSCAPE_TYPE_WATER_HIGH) {
					map.setHumanEnergyAt(map.getHumanEnergyAt(yTarget, xTarget) - 3, yTarget, xTarget);
					map.setHumanSatietyAt(map.getHumanSatietyAt(yTarget, xTarget) - 3, yTarget, xTarget);
				}
				else {
					map.setHumanEnergyAt(map.getHumanEnergyAt(yTarget, xTarget) - 2, yTarget, xTarget);
					map.setHumanSatietyAt(map.getHumanSatietyAt(yTarget, xTarget) - 2, yTarget, xTarget);
				}
				map.setHumanAgeAt(map.getHumanAgeAt(yTarget, xTarget) + 1, yTarget, xTarget);
				map.setActiveFlagHumanAt(0, yTarget, xTarget);
				return true;
			}
		}
		map.setHumanEnergyAt(map.getHumanEnergyAt(y, x) - 1, y, x);
		map.setHumanSatietyAt(map.getHumanSatietyAt(y, x) - 1, y, x);
		map.setHumanAgeAt(map.getHumanAgeAt(y, x) + 1, y, x);
		map.setActiveFlagHumanAt(0, y, x);
		return false;
	}
	
	// PLANT - MAKE FRUITS
	private static void tryToMakeFruits(long cellData, int y, int x) {
		if (date % 30 == 0) {
			MapPanel map = MainPanel.mapPanel;
			map.setPlantFruitsAt(map.getPlantFruitsAt(y, x) + ParamGenerator.generateInteger(5, 15), y, x);
		}
	}
	
	// PLANT - DROP FRUIT
	private static void tryToDropFruit(long cellData, int y, int x) {
		MapPanel map = MainPanel.mapPanel;
		if (map.getPlantFruitsAt(y, x) == 0) {
			return;
		}
		// 0.01 * 0.50 = 0.005
		boolean decision = ParamGenerator.generateBoolean(1) && ParamGenerator.generateBoolean(50);
		if (decision) {
			map.setPlantFruitsAt(map.getPlantFruitsAt(y, x) - 1, y, x);
			int yTarget = y + ParamGenerator.generateInteger(-2, 2);
			int xTarget = x + ParamGenerator.generateInteger(-2, 2);
			if (isCellInMapRange(yTarget, xTarget) && (yTarget != y || xTarget != x)) { 
				int landscapeTarget = map.getLandscapeTypeAt(yTarget, xTarget);
				int humanTarget = map.getHumanTypeAt(yTarget, xTarget);
				int plantTarget = map.getPlantTypeAt(yTarget, xTarget);
				if (landscapeTarget != DataCoder.LANDSCAPE_TYPE_WATER_LOW 
						&& landscapeTarget != DataCoder.LANDSCAPE_TYPE_WATER_HIGH
						&& humanTarget == DataCoder.HUMAN_TYPE_EMPTY 
						&& plantTarget == DataCoder.PLANT_TYPE_EMPTY) {
					map.setPlantTypeAt(DataCoder.PLANT_TYPE_APPLE, yTarget, xTarget);
				}
			}
		}
	}
	
	private static void moveHuman(int yFrom, int xFrom, int yTo, int xTo) {
		MapPanel map = MainPanel.mapPanel;
		map.setHumanTypeAt(map.getHumanTypeAt(yFrom, xFrom), yTo, xTo);
		map.setHumanAgeAt(map.getHumanAgeAt(yFrom, xFrom), yTo, xTo);
		map.setHumanEnergyAt(map.getHumanEnergyAt(yFrom, xFrom), yTo, xTo);
		map.setHumanSatietyAt(map.getHumanSatietyAt(yFrom, xFrom), yTo, xTo);
		map.setHumanPregnancyAt(map.getHumanPregnancyAt(yFrom, xFrom), yTo, xTo);
		clearHuman(yFrom, xFrom);
	}
	
	private static void clearHuman(int y, int x) {
		MapPanel map = MainPanel.mapPanel;
		map.setHumanTypeAt(0, y, x);
		map.setHumanAgeAt(0, y, x);
		map.setHumanEnergyAt(0, y, x);
		map.setHumanSatietyAt(0, y, x);
		map.setHumanPregnancyAt(0, y, x);
	}
	
	private static boolean isCellInMapRange(int y, int x) {
		return y >= 0 && y < MapPanel.MAP_SIZE && x >= 0 && x < MapPanel.MAP_SIZE;
	}
	
}
