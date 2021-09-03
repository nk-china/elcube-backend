package cn.nkpro.ts5.docengine;

public enum NkDocCycle{
        beforeCreate,
        afterCreated,

        beforeCalculate,
        afterCalculated,

        beforeUpdate,
        afterUpdated,
        afterUpdateCommitted,

        afterCopied,

        beforeDelete,
        afterDeleted,
        afterDeleteCommitted,
}