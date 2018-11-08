package root.iv.androidacademy;


import java.io.Serializable;

public class Category implements Serializable {
    private final String name;
    private final int colorRes;
    public static final Category[] SECTIONS = new Category[] {
            new Category("home",R.color.colorHome),
            new Category("opinion", R.color.colorOpinion),
            new Category("world",R.color.colorWorld),
            new Category("national",R.color.colorNational),
            new Category("politics",R.color.colorPolitics),
            new Category("upshot",R.color.colorUpshot),
            new Category("nyregion",R.color.colorNyregion),
            new Category("business",R.color.colorBusiness),
            new Category("technology",R.color.colorTechnology),
            new Category("science",R.color.colorScience),
            new Category("health",R.color.colorHealth),
            new Category("sports",R.color.colorSports),
            new Category("arts",R.color.colorArts),
            new Category("books",R.color.colorBooks),
            new Category("movies",R.color.colorMovies),
            new Category("theater",R.color.colorTheater),
            new Category("sundayreview",R.color.colorSundayreview),
            new Category("fashion",R.color.colorFashion),
            new Category("tmagazine",R.color.colorTmagazine),
            new Category("food",R.color.colorFood),
            new Category("travel",R.color.colorTravel),
            new Category("magazine",R.color.colorMagazine),
            new Category("realestate",R.color.colorRealestate),
            new Category("automobiles",R.color.colorAutomobiles),
            new Category("obituaries",R.color.colorObituaries),
            new Category("insider", R.color.colorInsider)
    };

    public static int getColorForSection(String section) {
        for (Category c : SECTIONS)
            if (c.name.equals(section))
                return c.colorRes;
        return SECTIONS[0].colorRes;
    }

    public Category(String name, int color) {
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

}