package com.chicken.invasion;

import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedramshirmohammad on 16-05-25.
 */
public class BackgroundHolder implements iHolder {

    private List<Background> backgrounds;
    private Background equippedBackground;

    public BackgroundHolder(ChickenInvasion controller, World world){
        backgrounds = new ArrayList<Background>();

        Background desert =  new Background("Desert","desertbackground500x900",150);
        desert.setPurchased(true);
        backgrounds.add(desert);

        Background farmville = new Background("FarmVille","farmvillebackground",300);
        backgrounds.add(farmville);

    }

    @Override
    public List<Background> getThrowables() {
        return backgrounds;
    }
}
