package root.iv.androidacademy;


import android.support.annotation.Nullable;

import java.io.Serializable;

public class Section implements Serializable {
    private final String name;
    private final int colorRes;
    public static final Section[] SECTIONS = new Section[] {
            new Section("home",R.color.colorHome),
            new Section("opinion", R.color.colorOpinion),
            new Section("world",R.color.colorWorld),
            new Section("national",R.color.colorNational),
            new Section("politics",R.color.colorPolitics),
            new Section("upshot",R.color.colorUpshot),
            new Section("nyregion",R.color.colorNyregion),
            new Section("business",R.color.colorBusiness),
            new Section("technology",R.color.colorTechnology),
            new Section("science",R.color.colorScience),
            new Section("health",R.color.colorHealth),
            new Section("sports",R.color.colorSports),
            new Section("arts",R.color.colorArts),
            new Section("books",R.color.colorBooks),
            new Section("movies",R.color.colorMovies),
            new Section("theater",R.color.colorTheater),
            new Section("sundayreview",R.color.colorSundayreview),
            new Section("fashion",R.color.colorFashion),
            new Section("tmagazine",R.color.colorTmagazine),
            new Section("food",R.color.colorFood),
            new Section("travel",R.color.colorTravel),
            new Section("magazine",R.color.colorMagazine),
            new Section("realestate",R.color.colorRealestate),
            new Section("automobiles",R.color.colorAutomobiles),
            new Section("obituaries",R.color.colorObituaries),
            new Section("insider", R.color.colorInsider)
    };

    public static int getColorForSection(String section) {
        for (Section c : SECTIONS)
            if (c.name.equals(section))
                return c.colorRes;
        return SECTIONS[0].colorRes;
    }

    private Section(String name, int color) {
        this.name = name;
        this.colorRes = color;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return colorRes;
    }

    @Nullable
    public static Section valueOf(String name, int colorRes) {
        for (Section section : SECTIONS)
            if (section.getName().equals(name) && section.getColor() == colorRes)
                return section;
        return null;
    }
}