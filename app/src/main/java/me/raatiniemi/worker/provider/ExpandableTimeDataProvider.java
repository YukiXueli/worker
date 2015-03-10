package me.raatiniemi.worker.provider;

public class ExpandableTimeDataProvider extends ExpandableDataProvider
{
    public static class TimeGroup extends Group
    {
        @Override
        public int getGroupId()
        {
            return 0;
        }

        @Override
        public String getText()
        {
            return null;
        }
    }

    public static class TimeChild extends Child
    {
        @Override
        public int getChildId()
        {
            return 0;
        }

        @Override
        public String getText()
        {
            return null;
        }
    }

    @Override
    public int getGroupCount()
    {
        return 0;
    }

    @Override
    public Group getGroupItem(int groupPosition)
    {
        return null;
    }

    @Override
    public int getChildCount(int groupPosition)
    {
        return 0;
    }

    @Override
    public Child getChildItem(int groupPosition, int childPosition)
    {
        return null;
    }
}
