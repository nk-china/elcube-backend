package cn.nkpro.ts5.engine.doc;

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